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
import web.practicafinal.models.Movie;
import web.practicafinal.models.Room;
import web.practicafinal.models.Ticket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import web.practicafinal.models.Session;
import web.practicafinal.models.controllers.exceptions.IllegalOrphanException;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class SessionJpaController implements Serializable {

    public SessionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Session session) throws RollbackFailureException, Exception {
        if (session.getTicketCollection() == null) {
            session.setTicketCollection(new ArrayList<Ticket>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Movie movieId = session.getMovieId();
            if (movieId != null) {
                movieId = em.getReference(movieId.getClass(), movieId.getId());
                session.setMovieId(movieId);
            }
            Room roomId = session.getRoomId();
            if (roomId != null) {
                roomId = em.getReference(roomId.getClass(), roomId.getId());
                session.setRoomId(roomId);
            }
            Collection<Ticket> attachedTicketCollection = new ArrayList<Ticket>();
            for (Ticket ticketCollectionTicketToAttach : session.getTicketCollection()) {
                ticketCollectionTicketToAttach = em.getReference(ticketCollectionTicketToAttach.getClass(), ticketCollectionTicketToAttach.getId());
                attachedTicketCollection.add(ticketCollectionTicketToAttach);
            }
            session.setTicketCollection(attachedTicketCollection);
            em.persist(session);
            if (movieId != null) {
                movieId.getSessionCollection().add(session);
                movieId = em.merge(movieId);
            }
            if (roomId != null) {
                roomId.getSessionCollection().add(session);
                roomId = em.merge(roomId);
            }
            for (Ticket ticketCollectionTicket : session.getTicketCollection()) {
                Session oldSessionIdOfTicketCollectionTicket = ticketCollectionTicket.getSessionId();
                ticketCollectionTicket.setSessionId(session);
                ticketCollectionTicket = em.merge(ticketCollectionTicket);
                if (oldSessionIdOfTicketCollectionTicket != null) {
                    oldSessionIdOfTicketCollectionTicket.getTicketCollection().remove(ticketCollectionTicket);
                    oldSessionIdOfTicketCollectionTicket = em.merge(oldSessionIdOfTicketCollectionTicket);
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

    public void edit(Session session) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Session persistentSession = em.find(Session.class, session.getId());
            Movie movieIdOld = persistentSession.getMovieId();
            Movie movieIdNew = session.getMovieId();
            Room roomIdOld = persistentSession.getRoomId();
            Room roomIdNew = session.getRoomId();
            Collection<Ticket> ticketCollectionOld = persistentSession.getTicketCollection();
            Collection<Ticket> ticketCollectionNew = session.getTicketCollection();
            List<String> illegalOrphanMessages = null;
            for (Ticket ticketCollectionOldTicket : ticketCollectionOld) {
                if (!ticketCollectionNew.contains(ticketCollectionOldTicket)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ticket " + ticketCollectionOldTicket + " since its sessionId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (movieIdNew != null) {
                movieIdNew = em.getReference(movieIdNew.getClass(), movieIdNew.getId());
                session.setMovieId(movieIdNew);
            }
            if (roomIdNew != null) {
                roomIdNew = em.getReference(roomIdNew.getClass(), roomIdNew.getId());
                session.setRoomId(roomIdNew);
            }
            Collection<Ticket> attachedTicketCollectionNew = new ArrayList<Ticket>();
            for (Ticket ticketCollectionNewTicketToAttach : ticketCollectionNew) {
                ticketCollectionNewTicketToAttach = em.getReference(ticketCollectionNewTicketToAttach.getClass(), ticketCollectionNewTicketToAttach.getId());
                attachedTicketCollectionNew.add(ticketCollectionNewTicketToAttach);
            }
            ticketCollectionNew = attachedTicketCollectionNew;
            session.setTicketCollection(ticketCollectionNew);
            session = em.merge(session);
            if (movieIdOld != null && !movieIdOld.equals(movieIdNew)) {
                movieIdOld.getSessionCollection().remove(session);
                movieIdOld = em.merge(movieIdOld);
            }
            if (movieIdNew != null && !movieIdNew.equals(movieIdOld)) {
                movieIdNew.getSessionCollection().add(session);
                movieIdNew = em.merge(movieIdNew);
            }
            if (roomIdOld != null && !roomIdOld.equals(roomIdNew)) {
                roomIdOld.getSessionCollection().remove(session);
                roomIdOld = em.merge(roomIdOld);
            }
            if (roomIdNew != null && !roomIdNew.equals(roomIdOld)) {
                roomIdNew.getSessionCollection().add(session);
                roomIdNew = em.merge(roomIdNew);
            }
            for (Ticket ticketCollectionNewTicket : ticketCollectionNew) {
                if (!ticketCollectionOld.contains(ticketCollectionNewTicket)) {
                    Session oldSessionIdOfTicketCollectionNewTicket = ticketCollectionNewTicket.getSessionId();
                    ticketCollectionNewTicket.setSessionId(session);
                    ticketCollectionNewTicket = em.merge(ticketCollectionNewTicket);
                    if (oldSessionIdOfTicketCollectionNewTicket != null && !oldSessionIdOfTicketCollectionNewTicket.equals(session)) {
                        oldSessionIdOfTicketCollectionNewTicket.getTicketCollection().remove(ticketCollectionNewTicket);
                        oldSessionIdOfTicketCollectionNewTicket = em.merge(oldSessionIdOfTicketCollectionNewTicket);
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
                Integer id = session.getId();
                if (findSession(id) == null) {
                    throw new NonexistentEntityException("The session with id " + id + " no longer exists.");
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
            Session session;
            try {
                session = em.getReference(Session.class, id);
                session.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The session with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ticket> ticketCollectionOrphanCheck = session.getTicketCollection();
            for (Ticket ticketCollectionOrphanCheckTicket : ticketCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Session (" + session + ") cannot be destroyed since the Ticket " + ticketCollectionOrphanCheckTicket + " in its ticketCollection field has a non-nullable sessionId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Movie movieId = session.getMovieId();
            if (movieId != null) {
                movieId.getSessionCollection().remove(session);
                movieId = em.merge(movieId);
            }
            Room roomId = session.getRoomId();
            if (roomId != null) {
                roomId.getSessionCollection().remove(session);
                roomId = em.merge(roomId);
            }
            em.remove(session);
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

    public List<Session> findSessionEntities() {
        return findSessionEntities(true, -1, -1);
    }

    public List<Session> findSessionEntities(int maxResults, int firstResult) {
        return findSessionEntities(false, maxResults, firstResult);
    }

    private List<Session> findSessionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Session.class));
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

    public Session findSession(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Session.class, id);
        } finally {
            em.close();
        }
    }

    public int getSessionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Session> rt = cq.from(Session.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
