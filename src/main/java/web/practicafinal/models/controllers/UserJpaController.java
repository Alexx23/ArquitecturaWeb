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
import web.practicafinal.models.Role;
import web.practicafinal.models.Ticket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import web.practicafinal.models.Comment;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.exceptions.IllegalOrphanException;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class UserJpaController implements Serializable {

    public UserJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws RollbackFailureException, Exception {
        if (user.getTicketCollection() == null) {
            user.setTicketCollection(new ArrayList<Ticket>());
        }
        if (user.getCommentCollection() == null) {
            user.setCommentCollection(new ArrayList<Comment>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Role roleId = user.getRoleId();
            if (roleId != null) {
                roleId = em.getReference(roleId.getClass(), roleId.getId());
                user.setRoleId(roleId);
            }
            Collection<Ticket> attachedTicketCollection = new ArrayList<Ticket>();
            for (Ticket ticketCollectionTicketToAttach : user.getTicketCollection()) {
                ticketCollectionTicketToAttach = em.getReference(ticketCollectionTicketToAttach.getClass(), ticketCollectionTicketToAttach.getId());
                attachedTicketCollection.add(ticketCollectionTicketToAttach);
            }
            user.setTicketCollection(attachedTicketCollection);
            Collection<Comment> attachedCommentCollection = new ArrayList<Comment>();
            for (Comment commentCollectionCommentToAttach : user.getCommentCollection()) {
                commentCollectionCommentToAttach = em.getReference(commentCollectionCommentToAttach.getClass(), commentCollectionCommentToAttach.getId());
                attachedCommentCollection.add(commentCollectionCommentToAttach);
            }
            user.setCommentCollection(attachedCommentCollection);
            em.persist(user);
            if (roleId != null) {
                roleId.getUserCollection().add(user);
                roleId = em.merge(roleId);
            }
            for (Ticket ticketCollectionTicket : user.getTicketCollection()) {
                User oldUserIdOfTicketCollectionTicket = ticketCollectionTicket.getUserId();
                ticketCollectionTicket.setUserId(user);
                ticketCollectionTicket = em.merge(ticketCollectionTicket);
                if (oldUserIdOfTicketCollectionTicket != null) {
                    oldUserIdOfTicketCollectionTicket.getTicketCollection().remove(ticketCollectionTicket);
                    oldUserIdOfTicketCollectionTicket = em.merge(oldUserIdOfTicketCollectionTicket);
                }
            }
            for (Comment commentCollectionComment : user.getCommentCollection()) {
                User oldUserIdOfCommentCollectionComment = commentCollectionComment.getUserId();
                commentCollectionComment.setUserId(user);
                commentCollectionComment = em.merge(commentCollectionComment);
                if (oldUserIdOfCommentCollectionComment != null) {
                    oldUserIdOfCommentCollectionComment.getCommentCollection().remove(commentCollectionComment);
                    oldUserIdOfCommentCollectionComment = em.merge(oldUserIdOfCommentCollectionComment);
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

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User persistentUser = em.find(User.class, user.getId());
            Role roleIdOld = persistentUser.getRoleId();
            Role roleIdNew = user.getRoleId();
            Collection<Ticket> ticketCollectionOld = persistentUser.getTicketCollection();
            Collection<Ticket> ticketCollectionNew = user.getTicketCollection();
            Collection<Comment> commentCollectionOld = persistentUser.getCommentCollection();
            Collection<Comment> commentCollectionNew = user.getCommentCollection();
            List<String> illegalOrphanMessages = null;
            for (Ticket ticketCollectionOldTicket : ticketCollectionOld) {
                if (!ticketCollectionNew.contains(ticketCollectionOldTicket)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ticket " + ticketCollectionOldTicket + " since its userId field is not nullable.");
                }
            }
            for (Comment commentCollectionOldComment : commentCollectionOld) {
                if (!commentCollectionNew.contains(commentCollectionOldComment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comment " + commentCollectionOldComment + " since its userId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (roleIdNew != null) {
                roleIdNew = em.getReference(roleIdNew.getClass(), roleIdNew.getId());
                user.setRoleId(roleIdNew);
            }
            Collection<Ticket> attachedTicketCollectionNew = new ArrayList<Ticket>();
            for (Ticket ticketCollectionNewTicketToAttach : ticketCollectionNew) {
                ticketCollectionNewTicketToAttach = em.getReference(ticketCollectionNewTicketToAttach.getClass(), ticketCollectionNewTicketToAttach.getId());
                attachedTicketCollectionNew.add(ticketCollectionNewTicketToAttach);
            }
            ticketCollectionNew = attachedTicketCollectionNew;
            user.setTicketCollection(ticketCollectionNew);
            Collection<Comment> attachedCommentCollectionNew = new ArrayList<Comment>();
            for (Comment commentCollectionNewCommentToAttach : commentCollectionNew) {
                commentCollectionNewCommentToAttach = em.getReference(commentCollectionNewCommentToAttach.getClass(), commentCollectionNewCommentToAttach.getId());
                attachedCommentCollectionNew.add(commentCollectionNewCommentToAttach);
            }
            commentCollectionNew = attachedCommentCollectionNew;
            user.setCommentCollection(commentCollectionNew);
            user = em.merge(user);
            if (roleIdOld != null && !roleIdOld.equals(roleIdNew)) {
                roleIdOld.getUserCollection().remove(user);
                roleIdOld = em.merge(roleIdOld);
            }
            if (roleIdNew != null && !roleIdNew.equals(roleIdOld)) {
                roleIdNew.getUserCollection().add(user);
                roleIdNew = em.merge(roleIdNew);
            }
            for (Ticket ticketCollectionNewTicket : ticketCollectionNew) {
                if (!ticketCollectionOld.contains(ticketCollectionNewTicket)) {
                    User oldUserIdOfTicketCollectionNewTicket = ticketCollectionNewTicket.getUserId();
                    ticketCollectionNewTicket.setUserId(user);
                    ticketCollectionNewTicket = em.merge(ticketCollectionNewTicket);
                    if (oldUserIdOfTicketCollectionNewTicket != null && !oldUserIdOfTicketCollectionNewTicket.equals(user)) {
                        oldUserIdOfTicketCollectionNewTicket.getTicketCollection().remove(ticketCollectionNewTicket);
                        oldUserIdOfTicketCollectionNewTicket = em.merge(oldUserIdOfTicketCollectionNewTicket);
                    }
                }
            }
            for (Comment commentCollectionNewComment : commentCollectionNew) {
                if (!commentCollectionOld.contains(commentCollectionNewComment)) {
                    User oldUserIdOfCommentCollectionNewComment = commentCollectionNewComment.getUserId();
                    commentCollectionNewComment.setUserId(user);
                    commentCollectionNewComment = em.merge(commentCollectionNewComment);
                    if (oldUserIdOfCommentCollectionNewComment != null && !oldUserIdOfCommentCollectionNewComment.equals(user)) {
                        oldUserIdOfCommentCollectionNewComment.getCommentCollection().remove(commentCollectionNewComment);
                        oldUserIdOfCommentCollectionNewComment = em.merge(oldUserIdOfCommentCollectionNewComment);
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
                Integer id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
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
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ticket> ticketCollectionOrphanCheck = user.getTicketCollection();
            for (Ticket ticketCollectionOrphanCheckTicket : ticketCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Ticket " + ticketCollectionOrphanCheckTicket + " in its ticketCollection field has a non-nullable userId field.");
            }
            Collection<Comment> commentCollectionOrphanCheck = user.getCommentCollection();
            for (Comment commentCollectionOrphanCheckComment : commentCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Comment " + commentCollectionOrphanCheckComment + " in its commentCollection field has a non-nullable userId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Role roleId = user.getRoleId();
            if (roleId != null) {
                roleId.getUserCollection().remove(user);
                roleId = em.merge(roleId);
            }
            em.remove(user);
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

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
