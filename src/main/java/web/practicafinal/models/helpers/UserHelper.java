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
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static User getUserById(int id) {
        UserHelper userHelper = new UserHelper();
        EntityManager em = userHelper.getEntityManager();
        
        TypedQuery<User> query = em.createNamedQuery("User.findById", User.class); 
        query.setParameter("id", id);
        List<User> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
    
    public static User getUserByUsername(String username) {
        UserHelper userHelper = new UserHelper();
        EntityManager em = userHelper.getEntityManager();
        
        TypedQuery<User> queryUsername = em.createNamedQuery("User.findByUsername", User.class); 
        queryUsername.setParameter("username", username);
        List<User> resultsUsername = queryUsername.getResultList();
        if (resultsUsername.size() <= 0) return null;
        return resultsUsername.get(0);
    }
    
    public static User getUserByEmail(String email) {
        UserHelper userHelper = new UserHelper();
        EntityManager em = userHelper.getEntityManager();
        
        TypedQuery<User> queryEmail = em.createNamedQuery("User.findByEmail", User.class); 
        queryEmail.setParameter("email", email);
        List<User> resultsEmail = queryEmail.getResultList();
        if (resultsEmail.size() <= 0) return null;
        return resultsEmail.get(0);
    }
    
    public static User getUserByUsernameEmail(String username) {
        UserHelper userHelper = new UserHelper();
        EntityManager em = userHelper.getEntityManager();
        
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username OR u.email = :email", User.class);
        query.setParameter("username", username);
        query.setParameter("email", username);
        List<User> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }

}
