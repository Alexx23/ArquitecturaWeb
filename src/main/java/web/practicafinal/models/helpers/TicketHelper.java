package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import web.practicafinal.models.Ticket;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.utils.CypherUtils;

/**
 *
 * @author Alex
 */
public class TicketHelper {
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static Ticket getTicketByCode(String code) {
        TicketHelper ticketHelper = new TicketHelper();
        EntityManager em = ticketHelper.getEntityManager();
        
        TypedQuery<Ticket> query = em.createNamedQuery("Ticket.findByCode", Ticket.class); 
        query.setParameter("code", code);
        List<Ticket> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
    
    public static String generateUniqueCode() {
        String code = "";
        boolean finish = false;
        while (!finish) {
            code = CypherUtils.randomString(20);
            Ticket ticketExists = getTicketByCode(code);
            if (ticketExists == null) finish = true;
        }
        return code;
    }

}
