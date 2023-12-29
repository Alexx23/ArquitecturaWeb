package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class UserHelper {
    
    private static EntityManager em = ModelController.getEMF().createEntityManager();
    
    public static User getUserByUsername(String username) {
        TypedQuery<User> queryUsername = em.createNamedQuery("User.findByUsername", User.class); 
        queryUsername.setParameter("username", username);
        List<User> resultsUsername = queryUsername.getResultList();
        if (resultsUsername.size() <= 0) return null;
        return resultsUsername.get(0);
    }
    
    public static User getUserByEmail(String email) {
        TypedQuery<User> queryEmail = em.createNamedQuery("User.findByEmail", User.class); 
        queryEmail.setParameter("email", email);
        List<User> resultsEmail = queryEmail.getResultList();
        if (resultsEmail.size() <= 0) return null;
        return resultsEmail.get(0);
    }
    
    public static User getUserByUsernameEmail(String username) {
        TypedQuery<User> query = em.createNamedQuery("User.findByUsernameEmail", User.class); 
        query.setParameter("username", username);
        query.setParameter("email", username);
        List<User> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
}
