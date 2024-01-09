package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import web.practicafinal.models.Movie;
import web.practicafinal.models.Session;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class SessionHelper {
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static List<Session> getByDay(Date date) {
        SessionHelper sessionHelper = new SessionHelper();
        EntityManager em = sessionHelper.getEntityManager();
        Date startDate = truncateTime(date);
        Calendar c = Calendar.getInstance(); 
        c.setTime(startDate); 
        c.add(Calendar.DATE, 1);
        Date finalDate = c.getTime();
        TypedQuery<Session> query = null;
        try {
        query = em.createQuery("SELECT s FROM Session s WHERE s.datetime >= :startDate AND s.datetime < :finalDate ORDER BY s.datetime ASC", Session.class); 
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        query.setParameter("startDate", startDate);
        query.setParameter("finalDate", finalDate);
        
        return query.getResultList();
    }
    
    public static List<Session> getByDayAndMovie(Date date, Movie movie) {
        SessionHelper sessionHelper = new SessionHelper();
        EntityManager em = sessionHelper.getEntityManager();
        
        Date startDate = truncateTime(date);
        Calendar c = Calendar.getInstance(); 
        c.setTime(startDate); 
        c.add(Calendar.DATE, 1);
        Date finalDate = c.getTime();
        
        TypedQuery<Session> query = em.createQuery("SELECT s FROM Session s WHERE s.datetime >= :startDate AND s.datetime < :finalDate AND s.movie = :movie ORDER BY s.datetime ASC", Session.class); 
        query.setParameter("startDate", startDate);
        query.setParameter("finalDate", finalDate);
        query.setParameter("movie", movie);
        
        return query.getResultList();
    }
    
    private static Date truncateTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    

}
