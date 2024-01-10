/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web.practicafinal.models.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.UserTransaction;
import web.practicafinal.models.User;
import web.practicafinal.models.Ticket;
import java.util.ArrayList;
import java.util.List;
import web.practicafinal.models.Payment;
import web.practicafinal.models.controllers.exceptions.IllegalOrphanException;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class PaymentJpaController implements Serializable {

    public PaymentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Payment payment) throws RollbackFailureException, Exception {
        if (payment.getTicketList() == null) {
            payment.setTicketList(new ArrayList<Ticket>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user = payment.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getId());
                payment.setUser(user);
            }
            List<Ticket> attachedTicketList = new ArrayList<Ticket>();
            for (Ticket ticketListTicketToAttach : payment.getTicketList()) {
                ticketListTicketToAttach = em.getReference(ticketListTicketToAttach.getClass(), ticketListTicketToAttach.getId());
                attachedTicketList.add(ticketListTicketToAttach);
            }
            payment.setTicketList(attachedTicketList);
            em.persist(payment);
            if (user != null) {
                user.getPaymentList().add(payment);
                user = em.merge(user);
            }
            for (Ticket ticketListTicket : payment.getTicketList()) {
                Payment oldPaymentOfTicketListTicket = ticketListTicket.getPayment();
                ticketListTicket.setPayment(payment);
                ticketListTicket = em.merge(ticketListTicket);
                if (oldPaymentOfTicketListTicket != null) {
                    oldPaymentOfTicketListTicket.getTicketList().remove(ticketListTicket);
                    oldPaymentOfTicketListTicket = em.merge(oldPaymentOfTicketListTicket);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Payment payment) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Payment persistentPayment = em.find(Payment.class, payment.getId());
            User userOld = persistentPayment.getUser();
            User userNew = payment.getUser();
            List<Ticket> ticketListOld = persistentPayment.getTicketList();
            List<Ticket> ticketListNew = payment.getTicketList();
            List<String> illegalOrphanMessages = null;
            for (Ticket ticketListOldTicket : ticketListOld) {
                if (!ticketListNew.contains(ticketListOldTicket)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ticket " + ticketListOldTicket + " since its payment field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getId());
                payment.setUser(userNew);
            }
            List<Ticket> attachedTicketListNew = new ArrayList<Ticket>();
            for (Ticket ticketListNewTicketToAttach : ticketListNew) {
                ticketListNewTicketToAttach = em.getReference(ticketListNewTicketToAttach.getClass(), ticketListNewTicketToAttach.getId());
                attachedTicketListNew.add(ticketListNewTicketToAttach);
            }
            ticketListNew = attachedTicketListNew;
            payment.setTicketList(ticketListNew);
            payment = em.merge(payment);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getPaymentList().remove(payment);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getPaymentList().add(payment);
                userNew = em.merge(userNew);
            }
            for (Ticket ticketListNewTicket : ticketListNew) {
                if (!ticketListOld.contains(ticketListNewTicket)) {
                    Payment oldPaymentOfTicketListNewTicket = ticketListNewTicket.getPayment();
                    ticketListNewTicket.setPayment(payment);
                    ticketListNewTicket = em.merge(ticketListNewTicket);
                    if (oldPaymentOfTicketListNewTicket != null && !oldPaymentOfTicketListNewTicket.equals(payment)) {
                        oldPaymentOfTicketListNewTicket.getTicketList().remove(ticketListNewTicket);
                        oldPaymentOfTicketListNewTicket = em.merge(oldPaymentOfTicketListNewTicket);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = payment.getId();
                if (findPayment(id) == null) {
                    throw new NonexistentEntityException("The payment with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Payment payment;
            try {
                payment = em.getReference(Payment.class, id);
                payment.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The payment with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ticket> ticketListOrphanCheck = payment.getTicketList();
            for (Ticket ticketListOrphanCheckTicket : ticketListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Payment (" + payment + ") cannot be destroyed since the Ticket " + ticketListOrphanCheckTicket + " in its ticketList field has a non-nullable payment field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            User user = payment.getUser();
            if (user != null) {
                user.getPaymentList().remove(payment);
                user = em.merge(user);
            }
            em.remove(payment);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Payment> findPaymentEntities() {
        return findPaymentEntities(true, -1, -1);
    }

    public List<Payment> findPaymentEntities(int maxResults, int firstResult) {
        return findPaymentEntities(false, maxResults, firstResult);
    }

    private List<Payment> findPaymentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Payment.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Payment findPayment(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Payment.class, id);
        } finally {
            em.close();
        }
    }

    public int getPaymentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Payment> rt = cq.from(Payment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
