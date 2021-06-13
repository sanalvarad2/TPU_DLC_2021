package DLC.TPI.DAO.Clases;

import java.util.List;
import DLC.TPI.Clases.Documento;
import DLC.TPI.Clases.Palabra;
import DLC.TPI.Clases.Posteo;
import DLC.TPI.DAO.Commons.DAOEclipseLink;


public class PosteoOAD extends DAOEclipseLink<Posteo, Integer> {
    
    public PosteoOAD() {
        super(Posteo.class);
    }
    
    public Posteo retrieve(Integer id){
        List<Posteo> resp = entityManager.createNamedQuery("Posteo.findById")
            .setParameter("idPosteo", id)
            .getResultList();
        if (resp.size() == 1) return resp.get(0);
        
        return null;
    }
    
    public Posteo retrieve(Palabra pal, Documento doc){
        List<Posteo> resp = entityManager.createNamedQuery("Posteo.findByPalabraAndDocumento")
            .setParameter("palabra", pal).setParameter("documento", doc)
            .getResultList();
        if (resp.size() == 1) return resp.get(0);
        
        return null;
    }
    
    public List<Posteo> retrieve(Palabra pal) {
        List<Posteo> resp = entityManager.createNamedQuery("Posteo.findByPalabra")
                .setParameter("palabra", pal)
                .getResultList();
        
        return resp;
    }
    
    public List<Posteo> retrieveOrdered(int cantidad, Palabra pal) {
        List<Posteo> resp = entityManager.createNamedQuery("Posteo.findByPalabraOrderedByTf")
                .setParameter("palabra", pal).setMaxResults(cantidad)
                .getResultList();
        
        return resp;
    }
    
    public List<Posteo> retrieve(Documento doc) {
        List<Posteo> resp = entityManager.createNamedQuery("Posteo.findByDocumento")
                .setParameter("documento", doc)
                .getResultList();
        
        return resp;
    }
    
    public Integer maxId() {
        List<Posteo> resp = entityManager.createNamedQuery("Posteo.findMaxId")
                .setMaxResults(1)
                .getResultList();
        if (resp == null || resp.size() == 0) return 0;
        
        Integer maxId = resp.get(0).getIdPosteo();
        
        return maxId;
    }
    @Override
    public List<Posteo> bulkCreate(List<Posteo> list){
        entityManager.getTransaction().begin();
        StringBuilder sb = new StringBuilder();
        String InsertQuery = "INSERT INTO posteos VALUES ";
        sb.append(InsertQuery);
        for(int i = 0; i<list.size(); i++) {
           Posteo item = list.get(i);
           sb.append(String.format("(%d, %d, %d, %d),", item.getIdPosteo(), item.getPalabra().getIdPalabra(),item.getDocumento().getIdDocumento(), item.getTf()));
           if( i %5000 == 0 || i== list.size()-1){
               
                String sql = sb.toString().substring(0, sb.length() - 1);
                entityManager.createNativeQuery(sql).executeUpdate();
                sb.delete(InsertQuery.length(), sb.length());
           }
        }
        entityManager.getTransaction().commit();
        return list;
        
    }
}