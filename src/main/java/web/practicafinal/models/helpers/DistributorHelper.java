package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import web.practicafinal.models.Distributor;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class DistributorHelper {
    
    private static EntityManager em = ModelController.getEMF().createEntityManager();
    
    public static Distributor getDistributorByName(String name) {
        TypedQuery<Distributor> query = em.createNamedQuery("Distributor.findByName", Distributor.class); 
        query.setParameter("name", name);
        List<Distributor> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
}
