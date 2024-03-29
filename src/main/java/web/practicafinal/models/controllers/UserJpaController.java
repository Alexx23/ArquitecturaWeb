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
import web.practicafinal.models.Payment;
import web.practicafinal.models.Favorite;
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
        if (user.getPaymentList() == null) {
            user.setPaymentList(new ArrayList<Payment>());
        }
        if (user.getFavoriteList() == null) {
            user.setFavoriteList(new ArrayList<Favorite>());
        }
        if (user.getCardList() == null) {
            user.setCardList(new ArrayList<Card>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Role role = user.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getId());
                user.setRole(role);
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
            List<Payment> attachedPaymentList = new ArrayList<Payment>();
            for (Payment paymentListPaymentToAttach : user.getPaymentList()) {
                paymentListPaymentToAttach = em.getReference(paymentListPaymentToAttach.getClass(), paymentListPaymentToAttach.getId());
                attachedPaymentList.add(paymentListPaymentToAttach);
            }
            user.setPaymentList(attachedPaymentList);
            List<Favorite> attachedFavoriteList = new ArrayList<Favorite>();
            for (Favorite favoriteListFavoriteToAttach : user.getFavoriteList()) {
                favoriteListFavoriteToAttach = em.getReference(favoriteListFavoriteToAttach.getClass(), favoriteListFavoriteToAttach.getId());
                attachedFavoriteList.add(favoriteListFavoriteToAttach);
            }
            user.setFavoriteList(attachedFavoriteList);
            List<Card> attachedCardList = new ArrayList<Card>();
            for (Card cardListCardToAttach : user.getCardList()) {
                cardListCardToAttach = em.getReference(cardListCardToAttach.getClass(), cardListCardToAttach.getId());
                attachedCardList.add(cardListCardToAttach);
            }
            user.setCardList(attachedCardList);
            em.persist(user);
            if (role != null) {
                role.getUserList().add(user);
                role = em.merge(role);
            }
            for (Ticket ticketListTicket : user.getTicketList()) {
                User oldUserOfTicketListTicket = ticketListTicket.getUser();
                ticketListTicket.setUser(user);
                ticketListTicket = em.merge(ticketListTicket);
                if (oldUserOfTicketListTicket != null) {
                    oldUserOfTicketListTicket.getTicketList().remove(ticketListTicket);
                    oldUserOfTicketListTicket = em.merge(oldUserOfTicketListTicket);
                }
            }
            for (Comment commentListComment : user.getCommentList()) {
                User oldUserOfCommentListComment = commentListComment.getUser();
                commentListComment.setUser(user);
                commentListComment = em.merge(commentListComment);
                if (oldUserOfCommentListComment != null) {
                    oldUserOfCommentListComment.getCommentList().remove(commentListComment);
                    oldUserOfCommentListComment = em.merge(oldUserOfCommentListComment);
                }
            }
            for (Payment paymentListPayment : user.getPaymentList()) {
                User oldUserOfPaymentListPayment = paymentListPayment.getUser();
                paymentListPayment.setUser(user);
                paymentListPayment = em.merge(paymentListPayment);
                if (oldUserOfPaymentListPayment != null) {
                    oldUserOfPaymentListPayment.getPaymentList().remove(paymentListPayment);
                    oldUserOfPaymentListPayment = em.merge(oldUserOfPaymentListPayment);
                }
            }
            for (Favorite favoriteListFavorite : user.getFavoriteList()) {
                User oldUserOfFavoriteListFavorite = favoriteListFavorite.getUser();
                favoriteListFavorite.setUser(user);
                favoriteListFavorite = em.merge(favoriteListFavorite);
                if (oldUserOfFavoriteListFavorite != null) {
                    oldUserOfFavoriteListFavorite.getFavoriteList().remove(favoriteListFavorite);
                    oldUserOfFavoriteListFavorite = em.merge(oldUserOfFavoriteListFavorite);
                }
            }
            for (Card cardListCard : user.getCardList()) {
                User oldUserOfCardListCard = cardListCard.getUser();
                cardListCard.setUser(user);
                cardListCard = em.merge(cardListCard);
                if (oldUserOfCardListCard != null) {
                    oldUserOfCardListCard.getCardList().remove(cardListCard);
                    oldUserOfCardListCard = em.merge(oldUserOfCardListCard);
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
            Role roleOld = persistentUser.getRole();
            Role roleNew = user.getRole();
            List<Ticket> ticketListOld = persistentUser.getTicketList();
            List<Ticket> ticketListNew = user.getTicketList();
            List<Comment> commentListOld = persistentUser.getCommentList();
            List<Comment> commentListNew = user.getCommentList();
            List<Payment> paymentListOld = persistentUser.getPaymentList();
            List<Payment> paymentListNew = user.getPaymentList();
            List<Favorite> favoriteListOld = persistentUser.getFavoriteList();
            List<Favorite> favoriteListNew = user.getFavoriteList();
            List<Card> cardListOld = persistentUser.getCardList();
            List<Card> cardListNew = user.getCardList();
            List<String> illegalOrphanMessages = null;
            for (Ticket ticketListOldTicket : ticketListOld) {
                if (!ticketListNew.contains(ticketListOldTicket)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ticket " + ticketListOldTicket + " since its user field is not nullable.");
                }
            }
            for (Comment commentListOldComment : commentListOld) {
                if (!commentListNew.contains(commentListOldComment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comment " + commentListOldComment + " since its user field is not nullable.");
                }
            }
            for (Payment paymentListOldPayment : paymentListOld) {
                if (!paymentListNew.contains(paymentListOldPayment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Payment " + paymentListOldPayment + " since its user field is not nullable.");
                }
            }
            for (Favorite favoriteListOldFavorite : favoriteListOld) {
                if (!favoriteListNew.contains(favoriteListOldFavorite)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Favorite " + favoriteListOldFavorite + " since its user field is not nullable.");
                }
            }
            for (Card cardListOldCard : cardListOld) {
                if (!cardListNew.contains(cardListOldCard)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Card " + cardListOldCard + " since its user field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getId());
                user.setRole(roleNew);
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
            List<Payment> attachedPaymentListNew = new ArrayList<Payment>();
            for (Payment paymentListNewPaymentToAttach : paymentListNew) {
                paymentListNewPaymentToAttach = em.getReference(paymentListNewPaymentToAttach.getClass(), paymentListNewPaymentToAttach.getId());
                attachedPaymentListNew.add(paymentListNewPaymentToAttach);
            }
            paymentListNew = attachedPaymentListNew;
            user.setPaymentList(paymentListNew);
            List<Favorite> attachedFavoriteListNew = new ArrayList<Favorite>();
            for (Favorite favoriteListNewFavoriteToAttach : favoriteListNew) {
                favoriteListNewFavoriteToAttach = em.getReference(favoriteListNewFavoriteToAttach.getClass(), favoriteListNewFavoriteToAttach.getId());
                attachedFavoriteListNew.add(favoriteListNewFavoriteToAttach);
            }
            favoriteListNew = attachedFavoriteListNew;
            user.setFavoriteList(favoriteListNew);
            List<Card> attachedCardListNew = new ArrayList<Card>();
            for (Card cardListNewCardToAttach : cardListNew) {
                cardListNewCardToAttach = em.getReference(cardListNewCardToAttach.getClass(), cardListNewCardToAttach.getId());
                attachedCardListNew.add(cardListNewCardToAttach);
            }
            cardListNew = attachedCardListNew;
            user.setCardList(cardListNew);
            user = em.merge(user);
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getUserList().remove(user);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getUserList().add(user);
                roleNew = em.merge(roleNew);
            }
            for (Ticket ticketListNewTicket : ticketListNew) {
                if (!ticketListOld.contains(ticketListNewTicket)) {
                    User oldUserOfTicketListNewTicket = ticketListNewTicket.getUser();
                    ticketListNewTicket.setUser(user);
                    ticketListNewTicket = em.merge(ticketListNewTicket);
                    if (oldUserOfTicketListNewTicket != null && !oldUserOfTicketListNewTicket.equals(user)) {
                        oldUserOfTicketListNewTicket.getTicketList().remove(ticketListNewTicket);
                        oldUserOfTicketListNewTicket = em.merge(oldUserOfTicketListNewTicket);
                    }
                }
            }
            for (Comment commentListNewComment : commentListNew) {
                if (!commentListOld.contains(commentListNewComment)) {
                    User oldUserOfCommentListNewComment = commentListNewComment.getUser();
                    commentListNewComment.setUser(user);
                    commentListNewComment = em.merge(commentListNewComment);
                    if (oldUserOfCommentListNewComment != null && !oldUserOfCommentListNewComment.equals(user)) {
                        oldUserOfCommentListNewComment.getCommentList().remove(commentListNewComment);
                        oldUserOfCommentListNewComment = em.merge(oldUserOfCommentListNewComment);
                    }
                }
            }
            for (Payment paymentListNewPayment : paymentListNew) {
                if (!paymentListOld.contains(paymentListNewPayment)) {
                    User oldUserOfPaymentListNewPayment = paymentListNewPayment.getUser();
                    paymentListNewPayment.setUser(user);
                    paymentListNewPayment = em.merge(paymentListNewPayment);
                    if (oldUserOfPaymentListNewPayment != null && !oldUserOfPaymentListNewPayment.equals(user)) {
                        oldUserOfPaymentListNewPayment.getPaymentList().remove(paymentListNewPayment);
                        oldUserOfPaymentListNewPayment = em.merge(oldUserOfPaymentListNewPayment);
                    }
                }
            }
            for (Favorite favoriteListNewFavorite : favoriteListNew) {
                if (!favoriteListOld.contains(favoriteListNewFavorite)) {
                    User oldUserOfFavoriteListNewFavorite = favoriteListNewFavorite.getUser();
                    favoriteListNewFavorite.setUser(user);
                    favoriteListNewFavorite = em.merge(favoriteListNewFavorite);
                    if (oldUserOfFavoriteListNewFavorite != null && !oldUserOfFavoriteListNewFavorite.equals(user)) {
                        oldUserOfFavoriteListNewFavorite.getFavoriteList().remove(favoriteListNewFavorite);
                        oldUserOfFavoriteListNewFavorite = em.merge(oldUserOfFavoriteListNewFavorite);
                    }
                }
            }
            for (Card cardListNewCard : cardListNew) {
                if (!cardListOld.contains(cardListNewCard)) {
                    User oldUserOfCardListNewCard = cardListNewCard.getUser();
                    cardListNewCard.setUser(user);
                    cardListNewCard = em.merge(cardListNewCard);
                    if (oldUserOfCardListNewCard != null && !oldUserOfCardListNewCard.equals(user)) {
                        oldUserOfCardListNewCard.getCardList().remove(cardListNewCard);
                        oldUserOfCardListNewCard = em.merge(oldUserOfCardListNewCard);
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
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Ticket " + ticketListOrphanCheckTicket + " in its ticketList field has a non-nullable user field.");
            }
            List<Comment> commentListOrphanCheck = user.getCommentList();
            for (Comment commentListOrphanCheckComment : commentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Comment " + commentListOrphanCheckComment + " in its commentList field has a non-nullable user field.");
            }
            List<Payment> paymentListOrphanCheck = user.getPaymentList();
            for (Payment paymentListOrphanCheckPayment : paymentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Payment " + paymentListOrphanCheckPayment + " in its paymentList field has a non-nullable user field.");
            }
            List<Favorite> favoriteListOrphanCheck = user.getFavoriteList();
            for (Favorite favoriteListOrphanCheckFavorite : favoriteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Favorite " + favoriteListOrphanCheckFavorite + " in its favoriteList field has a non-nullable user field.");
            }
            List<Card> cardListOrphanCheck = user.getCardList();
            for (Card cardListOrphanCheckCard : cardListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Card " + cardListOrphanCheckCard + " in its cardList field has a non-nullable user field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Role role = user.getRole();
            if (role != null) {
                role.getUserList().remove(user);
                role = em.merge(role);
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
