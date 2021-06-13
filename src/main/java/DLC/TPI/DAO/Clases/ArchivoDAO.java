/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DLC.TPI.DAO.Clases;

/**
 *
 * @author Santiago
 */
import java.util.List;
import DLC.TPI.Clases.Archivo;
import DLC.TPI.DAO.Commons.DAOEclipseLink;

/**
 *
 * @author Santiago
 */
public class ArchivoDAO extends DAOEclipseLink<Archivo, Integer> {
    
    public ArchivoDAO() {
        super(Archivo.class);
    }
    
    
     public String getFile(Integer id){
        String retVal = null;
        List<Archivo> resp = entityManager.createNamedQuery("Archivo.findById")
                .setParameter("idDocumento", id).getResultList();
        if (resp.size() == 1)
        { 
            retVal = resp.get(0).getFile();
        }
        
        return retVal;
    }
    
}
