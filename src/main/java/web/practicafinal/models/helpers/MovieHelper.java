package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import web.practicafinal.models.Movie;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class MovieHelper {
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static TypedQuery<Movie> getAvailablesMovies() {
        MovieHelper movieHelper = new MovieHelper();
        EntityManager em = movieHelper.getEntityManager();
        
        TypedQuery<Movie> query = em.createQuery("SELECT DISTINCT m FROM Movie m JOIN m.sessionList s WHERE s.datetime > :now", Movie.class); 
        query.setParameter("now", new Date());
        
        return query;
    }
    
    public static Query getAvailablesMoviesTotalCount() {
        MovieHelper movieHelper = new MovieHelper();
        EntityManager em = movieHelper.getEntityManager();
        
        Query query = em.createQuery("SELECT DISTINCT COUNT(m) FROM Movie m JOIN m.sessionList s WHERE s.datetime > :now", Movie.class); 
        query.setParameter("now", new Date());
        
        return query;
    }
    

}
