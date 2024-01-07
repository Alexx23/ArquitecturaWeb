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
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static Distributor getDistributorByName(String name) {
        DistributorHelper distributorHelper = new DistributorHelper();
        EntityManager em = distributorHelper.getEntityManager();
        
        TypedQuery<Distributor> query = em.createNamedQuery("Distributor.findByName", Distributor.class); 
        query.setParameter("name", name);
        List<Distributor> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
}
