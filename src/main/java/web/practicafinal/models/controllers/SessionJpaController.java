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
        if (session.getTicketList() == null) {
            session.setTicketList(new ArrayList<Ticket>());
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
            List<Ticket> attachedTicketList = new ArrayList<Ticket>();
            for (Ticket ticketListTicketToAttach : session.getTicketList()) {
                ticketListTicketToAttach = em.getReference(ticketListTicketToAttach.getClass(), ticketListTicketToAttach.getId());
                attachedTicketList.add(ticketListTicketToAttach);
            }
            session.setTicketList(attachedTicketList);
            em.persist(session);
            if (movieId != null) {
                movieId.getSessionList().add(session);
                movieId = em.merge(movieId);
            }
            if (roomId != null) {
                roomId.getSessionList().add(session);
                roomId = em.merge(roomId);
            }
            for (Ticket ticketListTicket : session.getTicketList()) {
                Session oldSessionIdOfTicketListTicket = ticketListTicket.getSessionId();
                ticketListTicket.setSessionId(session);
                ticketListTicket = em.merge(ticketListTicket);
                if (oldSessionIdOfTicketListTicket != null) {
                    oldSessionIdOfTicketListTicket.getTicketList().remove(ticketListTicket);
                    oldSessionIdOfTicketListTicket = em.merge(oldSessionIdOfTicketListTicket);
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
            List<Ticket> ticketListOld = persistentSession.getTicketList();
            List<Ticket> ticketListNew = session.getTicketList();
            List<String> illegalOrphanMessages = null;
            for (Ticket ticketListOldTicket : ticketListOld) {
                if (!ticketListNew.contains(ticketListOldTicket)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ticket " + ticketListOldTicket + " since its sessionId field is not nullable.");
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
            List<Ticket> attachedTicketListNew = new ArrayList<Ticket>();
            for (Ticket ticketListNewTicketToAttach : ticketListNew) {
                ticketListNewTicketToAttach = em.getReference(ticketListNewTicketToAttach.getClass(), ticketListNewTicketToAttach.getId());
                attachedTicketListNew.add(ticketListNewTicketToAttach);
            }
            ticketListNew = attachedTicketListNew;
            session.setTicketList(ticketListNew);
            session = em.merge(session);
            if (movieIdOld != null && !movieIdOld.equals(movieIdNew)) {
                movieIdOld.getSessionList().remove(session);
                movieIdOld = em.merge(movieIdOld);
            }
            if (movieIdNew != null && !movieIdNew.equals(movieIdOld)) {
                movieIdNew.getSessionList().add(session);
                movieIdNew = em.merge(movieIdNew);
            }
            if (roomIdOld != null && !roomIdOld.equals(roomIdNew)) {
                roomIdOld.getSessionList().remove(session);
                roomIdOld = em.merge(roomIdOld);
            }
            if (roomIdNew != null && !roomIdNew.equals(roomIdOld)) {
                roomIdNew.getSessionList().add(session);
                roomIdNew = em.merge(roomIdNew);
            }
            for (Ticket ticketListNewTicket : ticketListNew) {
                if (!ticketListOld.contains(ticketListNewTicket)) {
                    Session oldSessionIdOfTicketListNewTicket = ticketListNewTicket.getSessionId();
                    ticketListNewTicket.setSessionId(session);
                    ticketListNewTicket = em.merge(ticketListNewTicket);
                    if (oldSessionIdOfTicketListNewTicket != null && !oldSessionIdOfTicketListNewTicket.equals(session)) {
                        oldSessionIdOfTicketListNewTicket.getTicketList().remove(ticketListNewTicket);
                        oldSessionIdOfTicketListNewTicket = em.merge(oldSessionIdOfTicketListNewTicket);
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
            List<Ticket> ticketListOrphanCheck = session.getTicketList();
            for (Ticket ticketListOrphanCheckTicket : ticketListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Session (" + session + ") cannot be destroyed since the Ticket " + ticketListOrphanCheckTicket + " in its ticketList field has a non-nullable sessionId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Movie movieId = session.getMovieId();
            if (movieId != null) {
                movieId.getSessionList().remove(session);
                movieId = em.merge(movieId);
            }
            Room roomId = session.getRoomId();
            if (roomId != null) {
                roomId.getSessionList().remove(session);
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
