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
        if (room.getSessionList() == null) {
            room.setSessionList(new ArrayList<Session>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Session> attachedSessionList = new ArrayList<Session>();
            for (Session sessionListSessionToAttach : room.getSessionList()) {
                sessionListSessionToAttach = em.getReference(sessionListSessionToAttach.getClass(), sessionListSessionToAttach.getId());
                attachedSessionList.add(sessionListSessionToAttach);
            }
            room.setSessionList(attachedSessionList);
            em.persist(room);
            for (Session sessionListSession : room.getSessionList()) {
                Room oldRoomIdOfSessionListSession = sessionListSession.getRoomId();
                sessionListSession.setRoomId(room);
                sessionListSession = em.merge(sessionListSession);
                if (oldRoomIdOfSessionListSession != null) {
                    oldRoomIdOfSessionListSession.getSessionList().remove(sessionListSession);
                    oldRoomIdOfSessionListSession = em.merge(oldRoomIdOfSessionListSession);
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
            List<Session> sessionListOld = persistentRoom.getSessionList();
            List<Session> sessionListNew = room.getSessionList();
            List<String> illegalOrphanMessages = null;
            for (Session sessionListOldSession : sessionListOld) {
                if (!sessionListNew.contains(sessionListOldSession)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Session " + sessionListOldSession + " since its roomId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Session> attachedSessionListNew = new ArrayList<Session>();
            for (Session sessionListNewSessionToAttach : sessionListNew) {
                sessionListNewSessionToAttach = em.getReference(sessionListNewSessionToAttach.getClass(), sessionListNewSessionToAttach.getId());
                attachedSessionListNew.add(sessionListNewSessionToAttach);
            }
            sessionListNew = attachedSessionListNew;
            room.setSessionList(sessionListNew);
            room = em.merge(room);
            for (Session sessionListNewSession : sessionListNew) {
                if (!sessionListOld.contains(sessionListNewSession)) {
                    Room oldRoomIdOfSessionListNewSession = sessionListNewSession.getRoomId();
                    sessionListNewSession.setRoomId(room);
                    sessionListNewSession = em.merge(sessionListNewSession);
                    if (oldRoomIdOfSessionListNewSession != null && !oldRoomIdOfSessionListNewSession.equals(room)) {
                        oldRoomIdOfSessionListNewSession.getSessionList().remove(sessionListNewSession);
                        oldRoomIdOfSessionListNewSession = em.merge(oldRoomIdOfSessionListNewSession);
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
            List<Session> sessionListOrphanCheck = room.getSessionList();
            for (Session sessionListOrphanCheckSession : sessionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Room (" + room + ") cannot be destroyed since the Session " + sessionListOrphanCheckSession + " in its sessionList field has a non-nullable roomId field.");
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
