package tpudlc.classes.daos;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import tpudlc.classes.Palabra;
import tpudlc.dao.DaoEclipseLink;


public class PalabraDao extends DaoEclipseLink<Palabra, Integer> {
    
    public PalabraDao() {
        super(Palabra.class);
    }
    
    public Palabra retrieve(Integer id){
        List<Palabra> resp = entityManager.createNamedQuery("Palabra.findById")
            .setParameter("idPalabra", id)
            .getResultList();
        if (resp.size() == 1) return resp.get(0);
        
        return null;
    }
    
    public Palabra retrieve(String palabra){
        List<Palabra> resp = entityManager.createNamedQuery("Palabra.findByPalabra")
            .setParameter("palabra", palabra)
            .getResultList();
        if (resp.size() == 1) return resp.get(0);
        
        return null;
    }
    
    public Integer maxId() {
        List<Palabra> resp = entityManager.createNamedQuery("Palabra.findMaxId")
                .setMaxResults(1)
                .getResultList();
        if (resp == null || resp.size() == 0) return 0;
        
        Integer maxId = resp.get(0).getIdPalabra();
        
        return maxId;
    }
    
    @Override
    public List<Palabra> bulkCreate(List<Palabra> list){
        entityManager.getTransaction().begin();
        StringBuilder sb = new StringBuilder();
        String InsertQuery = "INSERT INTO palabras VALUES ";
        sb.append(InsertQuery);
        for(int i = 0; i<list.size(); i++) {
           Palabra item = list.get(i);
           sb.append(String.format("(%d, '%s', %d, %d),", item.getIdPalabra(), item.getPalabra(), item.getNr(), item.getMaxtf()));
           
           if( i %5000 == 0 || i== list.size()-1){
               
                String sql = sb.toString().substring(0, sb.length() - 1);
                entityManager.createNativeQuery(sql).executeUpdate();
                sb.delete(InsertQuery.length(), sb.length());
           }
        }
        entityManager.getTransaction().commit();
        return list;
        
    }
    
    public void deleteAll(){
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM palabras").executeUpdate();
        entityManager.getTransaction().commit();
    }
    
    
}