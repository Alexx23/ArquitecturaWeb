package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import web.practicafinal.models.Nationality;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class NationalityHelper {
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static Nationality getNationalityByName(String name) {
        NationalityHelper nationalityHelper = new NationalityHelper();
        EntityManager em = nationalityHelper.getEntityManager();
        
        TypedQuery<Nationality> query = em.createNamedQuery("Nationality.findByName", Nationality.class); 
        query.setParameter("name", name);
        List<Nationality> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
}
