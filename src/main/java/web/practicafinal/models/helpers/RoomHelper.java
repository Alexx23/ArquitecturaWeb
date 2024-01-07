package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import web.practicafinal.models.Room;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class RoomHelper {
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static Room getRoomByName(String name) {
        RoomHelper roomHelper = new RoomHelper();
        EntityManager em = roomHelper.getEntityManager();
        
        TypedQuery<Room> query = em.createNamedQuery("Room.findByName", Room.class); 
        query.setParameter("name", name);
        List<Room> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
}
