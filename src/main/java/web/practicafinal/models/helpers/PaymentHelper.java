/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import web.practicafinal.models.Payment;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class PaymentHelper {
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static Payment getPayment(User user, Date date) {
        PaymentHelper paymentHelper = new PaymentHelper();
        EntityManager em = paymentHelper.getEntityManager();
        
        TypedQuery<Payment> query = em.createQuery("SELECT p FROM Payment p WHERE p.user = :user AND p.createdAt = :date", Payment.class); 
        query.setParameter("user", user);
        query.setParameter("date", date);
        List<Payment> results = query.getResultList();
        if (results.size() <= 0) return null;
        return results.get(0);
    }
    
}
