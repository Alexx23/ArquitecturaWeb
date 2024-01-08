package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import web.practicafinal.models.Actor;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class ActorHelper {
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static List<Actor> getByIds(List<Integer> idList) {
        ActorHelper actorHelper = new ActorHelper();
        EntityManager em = actorHelper.getEntityManager();
        
        TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a WHERE a.id IN :idList", Actor.class); 
        query.setParameter("idList", idList);
        return query.getResultList();
    }
}
