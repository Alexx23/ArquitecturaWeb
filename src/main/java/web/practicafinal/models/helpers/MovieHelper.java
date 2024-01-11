package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    
    public static TypedQuery<Movie> getAvailablesMoviesQuery(String name) {
        MovieHelper movieHelper = new MovieHelper();
        EntityManager em = movieHelper.getEntityManager();
        
        TypedQuery<Movie> query = em.createQuery("SELECT DISTINCT m FROM Movie m JOIN m.sessionList s WHERE s.datetime > :now AND m.name LIKE :name", Movie.class); 
        query.setParameter("now", new Date());
        query.setParameter("name", "%"+name+"%");
        
        return query;
    }
    
    public static Query getAvailablesMoviesTotalCountQuery(String name) {
        MovieHelper movieHelper = new MovieHelper();
        EntityManager em = movieHelper.getEntityManager();
        
        Query query = em.createQuery("SELECT COUNT(DISTINCT m) FROM Movie m JOIN m.sessionList s WHERE s.datetime > :now AND m.name LIKE :name", Movie.class); 
        query.setParameter("now", new Date());
        query.setParameter("name", "%"+name+"%");
        
        return query;
    }
    
    
    public static List<Movie> getFilteredMoviesTQ(int actualPage, Map<String, List<Object>> filters) {
        int pageSize = 20;
        MovieHelper movieHelper = new MovieHelper();
        EntityManager em = movieHelper.getEntityManager();
        
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT m FROM Movie m LEFT JOIN m.sessionList s");
        
        if (filters != null && !filters.isEmpty()) {
            jpql.append(" WHERE");
            for (String key : filters.keySet()) {
                
                // Si el parámetro es una sala
                if (key.equalsIgnoreCase("room")) {
                    
                    List<Object> values = filters.get(key);
                    if (values != null && !values.isEmpty()) {
                        jpql.append(" s.room").append(" IN :").append(key).append(" AND");
                    }
                
                // Si el parámetro es un atributo normal
                } else {
                    
                    List<Object> values = filters.get(key);
                    if (values != null && !values.isEmpty()) {
                        jpql.append(" m.").append(key).append(" IN :").append(key).append(" AND");
                    }
                    
                }
                

            }
            jpql.delete(jpql.length() - 4, jpql.length()); // Elimina el " AND"
        }

        jpql.append(" ORDER BY m.id DESC");

        TypedQuery<Movie> query = em.createQuery(jpql.toString(), Movie.class);

        if (filters != null) {
            for (Map.Entry<String, List<Object>> entry : filters.entrySet()) {
                List<Object> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    query.setParameter(entry.getKey(), values);
                }
            }
        }
        
        query.setFirstResult((actualPage - 1) * pageSize);
        query.setMaxResults(pageSize);
        
        return query.getResultList();
    }
    
    public static long getFilteredMoviesTC(int actualPage, Map<String, List<Object>> filters) {
        int pageSize = 20;
        MovieHelper movieHelper = new MovieHelper();
        EntityManager em = movieHelper.getEntityManager();
        
        StringBuilder jpql = new StringBuilder("SELECT COUNT(DISTINCT m) FROM Movie m LEFT JOIN m.sessionList s");
        
        if (filters != null && !filters.isEmpty()) {
            jpql.append(" WHERE");
            for (String key : filters.keySet()) {
                
                // Si el parámetro es una sala
                if (key.equalsIgnoreCase("room")) {
                    
                    List<Object> values = filters.get(key);
                    if (values != null && !values.isEmpty()) {
                        jpql.append(" s.room").append(" IN :").append(key).append(" AND");
                    }

                // Si el parámetro es un atributo normal
                } else {
                    
                    List<Object> values = filters.get(key);
                    if (values != null && !values.isEmpty()) {
                        jpql.append(" m.").append(key).append(" IN :").append(key).append(" AND");
                    }
                    
                }
                

            }
            jpql.delete(jpql.length() - 4, jpql.length()); // Elimina el " AND"
        }

        jpql.append(" ORDER BY m.id DESC");
        
        Query query = em.createQuery(jpql.toString());

        if (filters != null) {
            for (Map.Entry<String, List<Object>> entry : filters.entrySet()) {
                List<Object> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    query.setParameter(entry.getKey(), values);
                }
            }
        }
        
        query.setFirstResult((actualPage - 1) * pageSize);
        query.setMaxResults(pageSize);
        
        return (long) query.getSingleResult();
    }

}
