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
            Movie movie = session.getMovie();
            if (movie != null) {
                movie = em.getReference(movie.getClass(), movie.getId());
                session.setMovie(movie);
            }
            Room room = session.getRoom();
            if (room != null) {
                room = em.getReference(room.getClass(), room.getId());
                session.setRoom(room);
            }
            List<Ticket> attachedTicketList = new ArrayList<Ticket>();
            for (Ticket ticketListTicketToAttach : session.getTicketList()) {
                ticketListTicketToAttach = em.getReference(ticketListTicketToAttach.getClass(), ticketListTicketToAttach.getId());
                attachedTicketList.add(ticketListTicketToAttach);
            }
            session.setTicketList(attachedTicketList);
            em.persist(session);
            if (movie != null) {
                movie.getSessionList().add(session);
                movie = em.merge(movie);
            }
            if (room != null) {
                room.getSessionList().add(session);
                room = em.merge(room);
            }
            for (Ticket ticketListTicket : session.getTicketList()) {
                Session oldSessionOfTicketListTicket = ticketListTicket.getSession();
                ticketListTicket.setSession(session);
                ticketListTicket = em.merge(ticketListTicket);
                if (oldSessionOfTicketListTicket != null) {
                    oldSessionOfTicketListTicket.getTicketList().remove(ticketListTicket);
                    oldSessionOfTicketListTicket = em.merge(oldSessionOfTicketListTicket);
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
            Movie movieOld = persistentSession.getMovie();
            Movie movieNew = session.getMovie();
            Room roomOld = persistentSession.getRoom();
            Room roomNew = session.getRoom();
            List<Ticket> ticketListOld = persistentSession.getTicketList();
            List<Ticket> ticketListNew = session.getTicketList();
            List<String> illegalOrphanMessages = null;
            for (Ticket ticketListOldTicket : ticketListOld) {
                if (!ticketListNew.contains(ticketListOldTicket)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ticket " + ticketListOldTicket + " since its session field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (movieNew != null) {
                movieNew = em.getReference(movieNew.getClass(), movieNew.getId());
                session.setMovie(movieNew);
            }
            if (roomNew != null) {
                roomNew = em.getReference(roomNew.getClass(), roomNew.getId());
                session.setRoom(roomNew);
            }
            List<Ticket> attachedTicketListNew = new ArrayList<Ticket>();
            for (Ticket ticketListNewTicketToAttach : ticketListNew) {
                ticketListNewTicketToAttach = em.getReference(ticketListNewTicketToAttach.getClass(), ticketListNewTicketToAttach.getId());
                attachedTicketListNew.add(ticketListNewTicketToAttach);
            }
            ticketListNew = attachedTicketListNew;
            session.setTicketList(ticketListNew);
            session = em.merge(session);
            if (movieOld != null && !movieOld.equals(movieNew)) {
                movieOld.getSessionList().remove(session);
                movieOld = em.merge(movieOld);
            }
            if (movieNew != null && !movieNew.equals(movieOld)) {
                movieNew.getSessionList().add(session);
                movieNew = em.merge(movieNew);
            }
            if (roomOld != null && !roomOld.equals(roomNew)) {
                roomOld.getSessionList().remove(session);
                roomOld = em.merge(roomOld);
            }
            if (roomNew != null && !roomNew.equals(roomOld)) {
                roomNew.getSessionList().add(session);
                roomNew = em.merge(roomNew);
            }
            for (Ticket ticketListNewTicket : ticketListNew) {
                if (!ticketListOld.contains(ticketListNewTicket)) {
                    Session oldSessionOfTicketListNewTicket = ticketListNewTicket.getSession();
                    ticketListNewTicket.setSession(session);
                    ticketListNewTicket = em.merge(ticketListNewTicket);
                    if (oldSessionOfTicketListNewTicket != null && !oldSessionOfTicketListNewTicket.equals(session)) {
                        oldSessionOfTicketListNewTicket.getTicketList().remove(ticketListNewTicket);
                        oldSessionOfTicketListNewTicket = em.merge(oldSessionOfTicketListNewTicket);
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
                illegalOrphanMessages.add("This Session (" + session + ") cannot be destroyed since the Ticket " + ticketListOrphanCheckTicket + " in its ticketList field has a non-nullable session field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Movie movie = session.getMovie();
            if (movie != null) {
                movie.getSessionList().remove(session);
                movie = em.merge(movie);
            }
            Room room = session.getRoom();
            if (room != null) {
                room.getSessionList().remove(session);
                room = em.merge(room);
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
