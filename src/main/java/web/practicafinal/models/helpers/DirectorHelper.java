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
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static Director getDirectorByName(String name) {
        DirectorHelper directorHelper = new DirectorHelper();
        EntityManager em = directorHelper.getEntityManager();
        
        TypedQuery<Director> query = em.createNamedQuery("Director.findByName", Director.class); 
        query.setParameter("name", name);
        List<Director> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
}
