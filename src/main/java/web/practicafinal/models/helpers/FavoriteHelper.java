package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import web.practicafinal.models.Favorite;
import web.practicafinal.models.Movie;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class FavoriteHelper {
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static Favorite getFavorite(User user, Movie movie) {
        FavoriteHelper favoriteHelper = new FavoriteHelper();
        EntityManager em = favoriteHelper.getEntityManager();
        
        TypedQuery<Favorite> query = em.createQuery("SELECT f FROM Favorite f WHERE f.user = :user AND f.movie = :movie", Favorite.class); 
        query.setParameter("user", user);
        query.setParameter("movie", movie);
        List<Favorite> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
    
    public static TypedQuery<Favorite> getFavAvailablesMoviesQuery(User user) {
        FavoriteHelper favoriteHelper = new FavoriteHelper();
        EntityManager em = favoriteHelper.getEntityManager();
        
        String jpql = "SELECT DISTINCT f FROM Favorite f JOIN Movie m ON f.movie.id = m.id JOIN Session s ON m.id = s.movie.id " +
              "WHERE f.user = :user AND s.datetime > :now ORDER BY f.createdAt DESC";
        
        
        TypedQuery<Favorite> query = em.createQuery(jpql, Favorite.class);

        query.setParameter("user", user);
        query.setParameter("now", new Date());
        
        return query;
    }
    
    public static Query getFavAvailablesMoviesTotalCountQuery(User user) {
        FavoriteHelper favoriteHelper = new FavoriteHelper();
        EntityManager em = favoriteHelper.getEntityManager();
        
        String jpql = "SELECT COUNT(DISTINCT f) FROM Favorite f JOIN Movie m ON f.movie.id = m.id JOIN Session s ON m.id = s.movie.id " +
              "WHERE f.user = :user AND s.datetime > :now ORDER BY f.createdAt DESC";
        
        
        Query query = em.createQuery(jpql);
            
        query.setParameter("user", user);
        query.setParameter("now", new Date());
        
        return query;
    }
    
}
