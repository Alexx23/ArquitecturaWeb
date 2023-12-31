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
import java.util.List;
import web.practicafinal.models.Comment;
import web.practicafinal.models.Card;
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
        if (user.getTicketList() == null) {
            user.setTicketList(new ArrayList<Ticket>());
        }
        if (user.getCommentList() == null) {
            user.setCommentList(new ArrayList<Comment>());
        }
        if (user.getCardList() == null) {
            user.setCardList(new ArrayList<Card>());
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
            List<Ticket> attachedTicketList = new ArrayList<Ticket>();
            for (Ticket ticketListTicketToAttach : user.getTicketList()) {
                ticketListTicketToAttach = em.getReference(ticketListTicketToAttach.getClass(), ticketListTicketToAttach.getId());
                attachedTicketList.add(ticketListTicketToAttach);
            }
            user.setTicketList(attachedTicketList);
            List<Comment> attachedCommentList = new ArrayList<Comment>();
            for (Comment commentListCommentToAttach : user.getCommentList()) {
                commentListCommentToAttach = em.getReference(commentListCommentToAttach.getClass(), commentListCommentToAttach.getId());
                attachedCommentList.add(commentListCommentToAttach);
            }
            user.setCommentList(attachedCommentList);
            List<Card> attachedCardList = new ArrayList<Card>();
            for (Card cardListCardToAttach : user.getCardList()) {
                cardListCardToAttach = em.getReference(cardListCardToAttach.getClass(), cardListCardToAttach.getId());
                attachedCardList.add(cardListCardToAttach);
            }
            user.setCardList(attachedCardList);
            em.persist(user);
            if (roleId != null) {
                roleId.getUserList().add(user);
                roleId = em.merge(roleId);
            }
            for (Ticket ticketListTicket : user.getTicketList()) {
                User oldUserIdOfTicketListTicket = ticketListTicket.getUserId();
                ticketListTicket.setUserId(user);
                ticketListTicket = em.merge(ticketListTicket);
                if (oldUserIdOfTicketListTicket != null) {
                    oldUserIdOfTicketListTicket.getTicketList().remove(ticketListTicket);
                    oldUserIdOfTicketListTicket = em.merge(oldUserIdOfTicketListTicket);
                }
            }
            for (Comment commentListComment : user.getCommentList()) {
                User oldUserIdOfCommentListComment = commentListComment.getUserId();
                commentListComment.setUserId(user);
                commentListComment = em.merge(commentListComment);
                if (oldUserIdOfCommentListComment != null) {
                    oldUserIdOfCommentListComment.getCommentList().remove(commentListComment);
                    oldUserIdOfCommentListComment = em.merge(oldUserIdOfCommentListComment);
                }
            }
            for (Card cardListCard : user.getCardList()) {
                User oldUserIdOfCardListCard = cardListCard.getUserId();
                cardListCard.setUserId(user);
                cardListCard = em.merge(cardListCard);
                if (oldUserIdOfCardListCard != null) {
                    oldUserIdOfCardListCard.getCardList().remove(cardListCard);
                    oldUserIdOfCardListCard = em.merge(oldUserIdOfCardListCard);
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
            List<Ticket> ticketListOld = persistentUser.getTicketList();
            List<Ticket> ticketListNew = user.getTicketList();
            List<Comment> commentListOld = persistentUser.getCommentList();
            List<Comment> commentListNew = user.getCommentList();
            List<Card> cardListOld = persistentUser.getCardList();
            List<Card> cardListNew = user.getCardList();
            List<String> illegalOrphanMessages = null;
            for (Ticket ticketListOldTicket : ticketListOld) {
                if (!ticketListNew.contains(ticketListOldTicket)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ticket " + ticketListOldTicket + " since its userId field is not nullable.");
                }
            }
            for (Comment commentListOldComment : commentListOld) {
                if (!commentListNew.contains(commentListOldComment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comment " + commentListOldComment + " since its userId field is not nullable.");
                }
            }
            for (Card cardListOldCard : cardListOld) {
                if (!cardListNew.contains(cardListOldCard)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Card " + cardListOldCard + " since its userId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (roleIdNew != null) {
                roleIdNew = em.getReference(roleIdNew.getClass(), roleIdNew.getId());
                user.setRoleId(roleIdNew);
            }
            List<Ticket> attachedTicketListNew = new ArrayList<Ticket>();
            for (Ticket ticketListNewTicketToAttach : ticketListNew) {
                ticketListNewTicketToAttach = em.getReference(ticketListNewTicketToAttach.getClass(), ticketListNewTicketToAttach.getId());
                attachedTicketListNew.add(ticketListNewTicketToAttach);
            }
            ticketListNew = attachedTicketListNew;
            user.setTicketList(ticketListNew);
            List<Comment> attachedCommentListNew = new ArrayList<Comment>();
            for (Comment commentListNewCommentToAttach : commentListNew) {
                commentListNewCommentToAttach = em.getReference(commentListNewCommentToAttach.getClass(), commentListNewCommentToAttach.getId());
                attachedCommentListNew.add(commentListNewCommentToAttach);
            }
            commentListNew = attachedCommentListNew;
            user.setCommentList(commentListNew);
            List<Card> attachedCardListNew = new ArrayList<Card>();
            for (Card cardListNewCardToAttach : cardListNew) {
                cardListNewCardToAttach = em.getReference(cardListNewCardToAttach.getClass(), cardListNewCardToAttach.getId());
                attachedCardListNew.add(cardListNewCardToAttach);
            }
            cardListNew = attachedCardListNew;
            user.setCardList(cardListNew);
            user = em.merge(user);
            if (roleIdOld != null && !roleIdOld.equals(roleIdNew)) {
                roleIdOld.getUserList().remove(user);
                roleIdOld = em.merge(roleIdOld);
            }
            if (roleIdNew != null && !roleIdNew.equals(roleIdOld)) {
                roleIdNew.getUserList().add(user);
                roleIdNew = em.merge(roleIdNew);
            }
            for (Ticket ticketListNewTicket : ticketListNew) {
                if (!ticketListOld.contains(ticketListNewTicket)) {
                    User oldUserIdOfTicketListNewTicket = ticketListNewTicket.getUserId();
                    ticketListNewTicket.setUserId(user);
                    ticketListNewTicket = em.merge(ticketListNewTicket);
                    if (oldUserIdOfTicketListNewTicket != null && !oldUserIdOfTicketListNewTicket.equals(user)) {
                        oldUserIdOfTicketListNewTicket.getTicketList().remove(ticketListNewTicket);
                        oldUserIdOfTicketListNewTicket = em.merge(oldUserIdOfTicketListNewTicket);
                    }
                }
            }
            for (Comment commentListNewComment : commentListNew) {
                if (!commentListOld.contains(commentListNewComment)) {
                    User oldUserIdOfCommentListNewComment = commentListNewComment.getUserId();
                    commentListNewComment.setUserId(user);
                    commentListNewComment = em.merge(commentListNewComment);
                    if (oldUserIdOfCommentListNewComment != null && !oldUserIdOfCommentListNewComment.equals(user)) {
                        oldUserIdOfCommentListNewComment.getCommentList().remove(commentListNewComment);
                        oldUserIdOfCommentListNewComment = em.merge(oldUserIdOfCommentListNewComment);
                    }
                }
            }
            for (Card cardListNewCard : cardListNew) {
                if (!cardListOld.contains(cardListNewCard)) {
                    User oldUserIdOfCardListNewCard = cardListNewCard.getUserId();
                    cardListNewCard.setUserId(user);
                    cardListNewCard = em.merge(cardListNewCard);
                    if (oldUserIdOfCardListNewCard != null && !oldUserIdOfCardListNewCard.equals(user)) {
                        oldUserIdOfCardListNewCard.getCardList().remove(cardListNewCard);
                        oldUserIdOfCardListNewCard = em.merge(oldUserIdOfCardListNewCard);
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
            List<Ticket> ticketListOrphanCheck = user.getTicketList();
            for (Ticket ticketListOrphanCheckTicket : ticketListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Ticket " + ticketListOrphanCheckTicket + " in its ticketList field has a non-nullable userId field.");
            }
            List<Comment> commentListOrphanCheck = user.getCommentList();
            for (Comment commentListOrphanCheckComment : commentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Comment " + commentListOrphanCheckComment + " in its commentList field has a non-nullable userId field.");
            }
            List<Card> cardListOrphanCheck = user.getCardList();
            for (Card cardListOrphanCheckCard : cardListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Card " + cardListOrphanCheckCard + " in its cardList field has a non-nullable userId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Role roleId = user.getRoleId();
            if (roleId != null) {
                roleId.getUserList().remove(user);
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
