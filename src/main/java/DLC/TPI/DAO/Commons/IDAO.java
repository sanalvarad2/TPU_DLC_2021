package DLC.TPI.DAO.Commons;

import java.util.List;



public interface IDAO<E extends DALEntity,K> {
    
    void update(E pData);
    
    void delete(K pKey);
    
    E create(E pData);
    
    E retrieve(K pKey);
    
    List<E> findAll();
    
}
