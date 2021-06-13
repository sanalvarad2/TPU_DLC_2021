package DLC.TPI.DAO.Clases;

import java.util.List;
import DLC.TPI.Clases.Documento;
import DLC.TPI.DAO.Commons.DaoEclipseLink;


public class DocumentoDAO extends DaoEclipseLink<Documento, Integer> {

    public DocumentoDAO() {
        super(Documento.class);
    }
    
    public Documento retrieve(Integer id){
        List<Documento> resp = entityManager.createNamedQuery("Documento.findById")
            .setParameter("idDocumento", id)
            .getResultList();
        if (resp.size() == 1) return resp.get(0);
        
        return null;
    }
    
    public Documento retrieve(String nombre) {
        List<Documento> resp = entityManager.createNamedQuery("Documento.findByNombreArchivo")
                .setParameter("nombreArchivo", nombre)
                .getResultList();
        if (resp.size() == 1) return resp.get(0);
        
        return null;
    }
    
    public Integer maxId() {
        List<Documento> resp = entityManager.createNamedQuery("Documento.findMaxId")
                .getResultList();
        if (resp == null || resp.size() == 0) return 0;
        
        Integer maxId = resp.get(0).getIdDocumento();
        
        return maxId;
    }
   
}
