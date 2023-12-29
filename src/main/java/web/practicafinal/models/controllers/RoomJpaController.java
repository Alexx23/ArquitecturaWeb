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
import web.practicafinal.models.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import web.practicafinal.models.Room;
import web.practicafinal.models.controllers.exceptions.IllegalOrphanException;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class RoomJpaController implements Serializable {

    public RoomJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Room room) throws RollbackFailureException, Exception {
        if (room.getSessionCollection() == null) {
            room.setSessionCollection(new ArrayList<Session>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Session> attachedSessionCollection = new ArrayList<Session>();
            for (Session sessionCollectionSessionToAttach : room.getSessionCollection()) {
                sessionCollectionSessionToAttach = em.getReference(sessionCollectionSessionToAttach.getClass(), sessionCollectionSessionToAttach.getId());
                attachedSessionCollection.add(sessionCollectionSessionToAttach);
            }
            room.setSessionCollection(attachedSessionCollection);
            em.persist(room);
            for (Session sessionCollectionSession : room.getSessionCollection()) {
                Room oldRoomIdOfSessionCollectionSession = sessionCollectionSession.getRoomId();
                sessionCollectionSession.setRoomId(room);
                sessionCollectionSession = em.merge(sessionCollectionSession);
                if (oldRoomIdOfSessionCollectionSession != null) {
                    oldRoomIdOfSessionCollectionSession.getSessionCollection().remove(sessionCollectionSession);
                    oldRoomIdOfSessionCollectionSession = em.merge(oldRoomIdOfSessionCollectionSession);
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

    public void edit(Room room) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Room persistentRoom = em.find(Room.class, room.getId());
            Collection<Session> sessionCollectionOld = persistentRoom.getSessionCollection();
            Collection<Session> sessionCollectionNew = room.getSessionCollection();
            List<String> illegalOrphanMessages = null;
            for (Session sessionCollectionOldSession : sessionCollectionOld) {
                if (!sessionCollectionNew.contains(sessionCollectionOldSession)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Session " + sessionCollectionOldSession + " since its roomId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Session> attachedSessionCollectionNew = new ArrayList<Session>();
            for (Session sessionCollectionNewSessionToAttach : sessionCollectionNew) {
                sessionCollectionNewSessionToAttach = em.getReference(sessionCollectionNewSessionToAttach.getClass(), sessionCollectionNewSessionToAttach.getId());
                attachedSessionCollectionNew.add(sessionCollectionNewSessionToAttach);
            }
            sessionCollectionNew = attachedSessionCollectionNew;
            room.setSessionCollection(sessionCollectionNew);
            room = em.merge(room);
            for (Session sessionCollectionNewSession : sessionCollectionNew) {
                if (!sessionCollectionOld.contains(sessionCollectionNewSession)) {
                    Room oldRoomIdOfSessionCollectionNewSession = sessionCollectionNewSession.getRoomId();
                    sessionCollectionNewSession.setRoomId(room);
                    sessionCollectionNewSession = em.merge(sessionCollectionNewSession);
                    if (oldRoomIdOfSessionCollectionNewSession != null && !oldRoomIdOfSessionCollectionNewSession.equals(room)) {
                        oldRoomIdOfSessionCollectionNewSession.getSessionCollection().remove(sessionCollectionNewSession);
                        oldRoomIdOfSessionCollectionNewSession = em.merge(oldRoomIdOfSessionCollectionNewSession);
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
                Integer id = room.getId();
                if (findRoom(id) == null) {
                    throw new NonexistentEntityException("The room with id " + id + " no longer exists.");
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
            Room room;
            try {
                room = em.getReference(Room.class, id);
                room.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The room with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Session> sessionCollectionOrphanCheck = room.getSessionCollection();
            for (Session sessionCollectionOrphanCheckSession : sessionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Room (" + room + ") cannot be destroyed since the Session " + sessionCollectionOrphanCheckSession + " in its sessionCollection field has a non-nullable roomId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(room);
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

    public List<Room> findRoomEntities() {
        return findRoomEntities(true, -1, -1);
    }

    public List<Room> findRoomEntities(int maxResults, int firstResult) {
        return findRoomEntities(false, maxResults, firstResult);
    }

    private List<Room> findRoomEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Room.class));
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

    public Room findRoom(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Room.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoomCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Room> rt = cq.from(Room.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
