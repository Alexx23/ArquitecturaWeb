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
import java.util.List;
import web.practicafinal.models.Session;
import web.practicafinal.models.Ticket;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class TicketJpaController implements Serializable {

    public TicketJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ticket ticket) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Session sessionId = ticket.getSessionId();
            if (sessionId != null) {
                sessionId = em.getReference(sessionId.getClass(), sessionId.getId());
                ticket.setSessionId(sessionId);
            }
            User userId = ticket.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                ticket.setUserId(userId);
            }
            em.persist(ticket);
            if (sessionId != null) {
                sessionId.getTicketList().add(ticket);
                sessionId = em.merge(sessionId);
            }
            if (userId != null) {
                userId.getTicketList().add(ticket);
                userId = em.merge(userId);
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

    public void edit(Ticket ticket) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ticket persistentTicket = em.find(Ticket.class, ticket.getId());
            Session sessionIdOld = persistentTicket.getSessionId();
            Session sessionIdNew = ticket.getSessionId();
            User userIdOld = persistentTicket.getUserId();
            User userIdNew = ticket.getUserId();
            if (sessionIdNew != null) {
                sessionIdNew = em.getReference(sessionIdNew.getClass(), sessionIdNew.getId());
                ticket.setSessionId(sessionIdNew);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                ticket.setUserId(userIdNew);
            }
            ticket = em.merge(ticket);
            if (sessionIdOld != null && !sessionIdOld.equals(sessionIdNew)) {
                sessionIdOld.getTicketList().remove(ticket);
                sessionIdOld = em.merge(sessionIdOld);
            }
            if (sessionIdNew != null && !sessionIdNew.equals(sessionIdOld)) {
                sessionIdNew.getTicketList().add(ticket);
                sessionIdNew = em.merge(sessionIdNew);
            }
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getTicketList().remove(ticket);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getTicketList().add(ticket);
                userIdNew = em.merge(userIdNew);
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
                Integer id = ticket.getId();
                if (findTicket(id) == null) {
                    throw new NonexistentEntityException("The ticket with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ticket ticket;
            try {
                ticket = em.getReference(Ticket.class, id);
                ticket.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ticket with id " + id + " no longer exists.", enfe);
            }
            Session sessionId = ticket.getSessionId();
            if (sessionId != null) {
                sessionId.getTicketList().remove(ticket);
                sessionId = em.merge(sessionId);
            }
            User userId = ticket.getUserId();
            if (userId != null) {
                userId.getTicketList().remove(ticket);
                userId = em.merge(userId);
            }
            em.remove(ticket);
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

    public List<Ticket> findTicketEntities() {
        return findTicketEntities(true, -1, -1);
    }

    public List<Ticket> findTicketEntities(int maxResults, int firstResult) {
        return findTicketEntities(false, maxResults, firstResult);
    }

    private List<Ticket> findTicketEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ticket.class));
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

    public Ticket findTicket(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ticket.class, id);
        } finally {
            em.close();
        }
    }

    public int getTicketCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ticket> rt = cq.from(Ticket.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
