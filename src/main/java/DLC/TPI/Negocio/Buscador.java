package DLC.TPI.Negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import DLC.TPI.Clases.Documento;
import DLC.TPI.Clases.Palabra;
import DLC.TPI.Clases.Posteo;
import DLC.TPI.DAO.Clases.DocumentoDAO;
import DLC.TPI.DAO.Clases.PalabraDAO;
import DLC.TPI.DAO.Clases.PosteoDAO;

public class Buscador {

    @Inject
    private DocumentoDAO documentoDAO;
    @Inject
    private PalabraDAO palabraDAO;
    @Inject
    private PosteoDAO posteoDAO;

    public List<Documento> buscar(String query) {

        // Calculo la cantidad total de documentos.
        Integer n = documentoDAO.getCount();

        List<Documento> respuesta = new ArrayList();

        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(query.toLowerCase());
        ArrayList<Palabra> listaPalabras = new ArrayList(10);
        
        while (m.find()) {
            String termino = m.group().toLowerCase();
            Palabra palabra = palabraDAO.retrieve(termino);
            if (palabra != null) {
                listaPalabras.add(palabra);
            }
        }

        if (listaPalabras.size() > 0) {
            // Ordena las palabras de la query de acuerdo a su Nr, en orden ascendente.
            Collections.sort(listaPalabras, new ComparadorNrPalabras());

            // El HashMap que contendrá a los documentos relevantes y sus puntajes.
            HashMap<Documento, Double> hashmapDocumentos = new HashMap(200);

            for (Palabra palabra : listaPalabras) {

                // Para cada palabra, obtiene los 10 posteos con el tf más alto.
                ArrayList<Posteo> listaPosteos = new ArrayList(posteoDAO.retrieveOrdered(10, palabra));
                
                for (Posteo posteo : listaPosteos) {
                    Double denominador = (double) palabra.getNr().intValue();

                    Double log = Math.log10(n / denominador);

                    Double puntaje = (Double) (posteo.getTf() * log);

                    Documento documento = posteo.getDocumento();
                    if (!hashmapDocumentos.containsKey(documento)) {
                        hashmapDocumentos.put(documento, 0.0);
                    }

                    // Suma el puntaje al documento correspondiente.
                    Double puntajeTemporal = hashmapDocumentos.get(documento) + puntaje;
                    hashmapDocumentos.remove(documento);
                    hashmapDocumentos.put(documento, puntajeTemporal);
                }
                
            }
            
            /* Se convierte el HashMap en una lista para ordenar los documentos
               en base a los puntajes.
             */
            ArrayList<ContenedorDocumento> listaDocumentos = new ArrayList();

            Iterator it = hashmapDocumentos.entrySet().iterator();
            
            while (it.hasNext()) {
                Map.Entry<Documento, Double> tupla = (Map.Entry) it.next();
                Documento documento = tupla.getKey();
                Double puntaje = tupla.getValue();

                ContenedorDocumento contenedor = new ContenedorDocumento(documento, puntaje);
                listaDocumentos.add(contenedor);

                it.remove();
            }

            // Ahora los ordena en base a sus respectivos puntajes.
            Collections.sort(listaDocumentos, new ComparadorPuntajeDocumentos());

            for (int i = listaDocumentos.size() - 1; i > -1; i--) {
                Documento documentoRespuesta = listaDocumentos.get(i).getDocumento();
                respuesta.add(documentoRespuesta);
            }
        }

        return respuesta;
        
    }
    
    private class ContenedorDocumento {

        private Documento documento;
        private Double puntaje;

        public ContenedorDocumento(Documento documento, Double puntaje) {
            this.documento = documento;
            this.puntaje = puntaje;
        }

        public Documento getDocumento() {
            return documento;
        }

        public Double getPuntaje() {
            return puntaje;
        }

    }

    private class ComparadorNrPalabras implements Comparator<Palabra> {
        
        @Override
        public int compare(Palabra p1, Palabra p2) {
            return p1.getNr().compareTo(p2.getNr());
        }
        
    }

    private class ComparadorPuntajeDocumentos implements Comparator<ContenedorDocumento> {

        @Override
        public int compare(ContenedorDocumento p1, ContenedorDocumento p2) {
            return p1.getPuntaje().compareTo(p2.getPuntaje());
        }
        
    }

}
