package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import web.practicafinal.models.Genre;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class GenreHelper {
    
    private static EntityManager em = ModelController.getEMF().createEntityManager();
    
    public static Genre getGenreByName(String name) {
        TypedQuery<Genre> query = em.createNamedQuery("Genre.findByName", Genre.class); 
        query.setParameter("name", name);
        List<Genre> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
}
