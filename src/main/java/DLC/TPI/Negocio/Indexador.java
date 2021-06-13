package DLC.TPI.Negocio;

import DLC.TPI.Clases.Archivo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import DLC.TPI.Clases.Documento;
import DLC.TPI.Clases.Palabra;
import DLC.TPI.Clases.Posteo;
import DLC.TPI.DAO.Clases.ArchivoDAO;
import DLC.TPI.DAO.Clases.DocumentoDAO;
import DLC.TPI.DAO.Clases.PalabraDAO;
import DLC.TPI.DAO.Clases.PosteoDAO;
import DLC.TPI.Utils.FileToBase64;

public class Indexador {

    @Inject
    private ArchivoDAO archivoDAO;
    @Inject
    private DocumentoDAO documentoDAO;
    @Inject
    private PalabraDAO palabraDAO;
    @Inject
    private PosteoDAO posteoDAO;

    public static final String directorioDocumentos = "E:\\DocumentosTP1\\";
    private int cantidadArchivosIndexados;

    public int indexar() throws IOException {

        cantidadArchivosIndexados = 0;
        File fileDirectorioDocumentoss = new File(directorioDocumentos);

        File[] arrayDocumentos = fileDirectorioDocumentoss.listFiles();

        ArrayList<File> listaDocumentos = new ArrayList();

        for (int i = 0; i < arrayDocumentos.length; i++) {
            if (!arrayDocumentos[i].isDirectory()) {
                listaDocumentos.add(arrayDocumentos[i]);
            }
        }

        Pattern patron = Pattern.compile("[a-zA-Z]+");
        Integer j = 0;

        /* Para poder hacer bulk inserts, es necesario asignar manualmente las
           claves primarias, por lo que es necesario obtener el id más alto
           de las palabras y de los posteos. Si la tabla está vacía, maxId()
           devuelve 0.
         */
        Integer idDocumento = documentoDAO.maxId() + 1;
        Integer idPalabra = palabraDAO.maxId() + 1;
        Integer idPosteo = posteoDAO.maxId() + 1;

        if (listaDocumentos.size() > 0) {
            HashMap<String, Palabra> hashmapVocabulario = iniciarVocabulario();
            HashMap<String, Documento> hashmapDocumentos = iniciarDocumentos();
            
            System.out.println("Vocabulario inicializado con éxito.");

            for (File d : listaDocumentos) {
                
                System.gc();
                System.out.println("Documento: " + d.getName());
                
                Documento documento = hashmapDocumentos.get(d.getName());
                
                if (documento == null) {
                    documento = new Documento(d.getName());
                    documento.setIdDocumento(idDocumento);
                    idDocumento ++;
                    hashmapDocumentos.put(d.getName(), documento);

                    /* TODAS las tablas hash y arrays  de los cuales se sabe que van
                     * a contener una cantidad muy grande de objetos, son
                     * inicializados con un tamaño exagerado, para evitar que sean
                     * necesarias las operaciones rehash y resize.
                     */
                    HashMap<String, Posteo> hashmapPosteos = new HashMap(100000, 0.5f);

                    ArrayList<Palabra> listaPalabrasPendientesCreacion = new ArrayList(50000);

                    BufferedReader lector = new BufferedReader(new FileReader(d));
                    String renglon;
                    String primerasLineas = "";
                    
                    int contadorLineas = 0;
                    
                    while ((renglon = lector.readLine()) != null) {

                        if (0 <= contadorLineas && contadorLineas <= 2) {
                            primerasLineas += renglon + "- ";
                        }

                        Matcher m = patron.matcher(renglon);

                        while (m.find()) {

                            String p = m.group().toLowerCase();
                            Palabra palabra = hashmapVocabulario.get(p);
                            
                            if (palabra == null) {
                                palabra = new Palabra(idPalabra, p);
                                idPalabra++;
                                listaPalabrasPendientesCreacion.add(palabra);
                                hashmapVocabulario.put(palabra.getPalabra(), palabra);
                            }

                            Posteo posteo = hashmapPosteos.get(p);
                            
                            if (posteo == null) {
                                posteo = new Posteo(idPosteo, palabra, documento);
                                idPosteo++;
                                hashmapPosteos.put(p, posteo);
                            }

                            posteo.increaseTf();

                            if (j % 10000 == 0) {
                                System.out.println("Número aproximado de palabras indexadas: " + j.toString());
                                Integer tamanoVoc = (Integer) hashmapVocabulario.size();
                                System.out.println("Palabras distintas:" + tamanoVoc.toString());
                            }

                            j++;
                        }
                        contadorLineas++;
                    }
                    
                    lector.close();
                    documento.setPrimerasLineas(primerasLineas);
                    documento = documentoDAO.create(documento);
                    
                    archivoDAO.create(new Archivo(documento.getIdDocumento(), FileToBase64.encodeFileToBase64(d)));
                    
                    /* Para insertar un posteo en la base de datos, es necesario que la palabra
                     * y el documento que le corresponden ya se encuentren ahí.
                     * Por eso, primero insertamos en la db todas las palabras nuevas, y recién
                     * luego insertamos los posteos.
                     */
                    updatePalabras(hashmapPosteos);

                    persistirPosteos(hashmapPosteos);

                    System.out.println("Se indexó el documento: " + d.getName());
                    cantidadArchivosIndexados++;
                }
                
            }
            
            persistirPalabras(hashmapVocabulario);
        }

        System.out.println("Proceso de indexación finalizado con éxito.");
        return cantidadArchivosIndexados;
    }

    private HashMap<String, Palabra> iniciarVocabulario() {
        
        HashMap<String, Palabra> hashmapVocabulario = new HashMap(800000, 0.5f);
        
        ArrayList<Palabra> queryPalabras = new ArrayList<Palabra>(palabraDAO.findAll());
        Integer queryLength = queryPalabras.size();
        
        for (int i = 0; i < queryLength; i++) {
            String stringPalabra = queryPalabras.get(i).getPalabra();
            Palabra objetoPalabra = queryPalabras.get(i);
            hashmapVocabulario.put(stringPalabra, objetoPalabra);
        }

        return hashmapVocabulario;
        
    }

    private HashMap<String, Documento> iniciarDocumentos() {
        
        HashMap<String, Documento> hashmapDocumentos = new HashMap(800000, 0.5f);
        
        ArrayList<Documento> queryDocumentos = new ArrayList<Documento>(documentoDAO.findAll());
        
        queryDocumentos.forEach(doc -> {
            hashmapDocumentos.put(doc.getNombreArchivo(), doc);
        });
        
        return hashmapDocumentos;
        
    }

    private void updatePalabras(HashMap<String, Posteo> hashmapPosteos) {
        
        Collection<Posteo> coleccionPosteos = hashmapPosteos.values();
        
        coleccionPosteos.forEach(posteo -> {
            Palabra palabra = posteo.getPalabra();
            palabra.increaseNr();
            
            if (posteo.getTf() > palabra.getMaxtf()) {
                palabra.setMaxtf(posteo.getTf());
            }
        });
        
    }

    private void persistirPosteos(HashMap<String, Posteo> hashmapPosteos) {
        
        Collection<Posteo> values = hashmapPosteos.values();
        
        ArrayList<Posteo> valuesList = new ArrayList<Posteo>(values);
        posteoDAO.bulkCreate(valuesList);
        
    }

    private void persistirPalabras(HashMap<String, Palabra> hashmapVocabulario) {
        
        palabraDAO.deleteAll();
        
        Collection<Palabra> values = hashmapVocabulario.values();
        
        ArrayList<Palabra> valuesList = new ArrayList<Palabra>(values);
        palabraDAO.bulkCreate(valuesList);

    }
}
