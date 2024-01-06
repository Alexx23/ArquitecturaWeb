package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import web.practicafinal.models.Director;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class DirectorHelper {
    
    private static EntityManager em = ModelController.getEMF().createEntityManager();
    
    public static Director getDirectorByName(String name) {
        TypedQuery<Director> query = em.createNamedQuery("Director.findByName", Director.class); 
        query.setParameter("name", name);
        List<Director> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
}
