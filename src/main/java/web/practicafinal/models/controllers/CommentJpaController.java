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
import web.practicafinal.models.Comment;
import web.practicafinal.models.Movie;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class CommentJpaController implements Serializable {

    public CommentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Comment comment) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Movie movieId = comment.getMovieId();
            if (movieId != null) {
                movieId = em.getReference(movieId.getClass(), movieId.getId());
                comment.setMovieId(movieId);
            }
            User userId = comment.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                comment.setUserId(userId);
            }
            em.persist(comment);
            if (movieId != null) {
                movieId.getCommentList().add(comment);
                movieId = em.merge(movieId);
            }
            if (userId != null) {
                userId.getCommentList().add(comment);
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

    public void edit(Comment comment) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Comment persistentComment = em.find(Comment.class, comment.getId());
            Movie movieIdOld = persistentComment.getMovieId();
            Movie movieIdNew = comment.getMovieId();
            User userIdOld = persistentComment.getUserId();
            User userIdNew = comment.getUserId();
            if (movieIdNew != null) {
                movieIdNew = em.getReference(movieIdNew.getClass(), movieIdNew.getId());
                comment.setMovieId(movieIdNew);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                comment.setUserId(userIdNew);
            }
            comment = em.merge(comment);
            if (movieIdOld != null && !movieIdOld.equals(movieIdNew)) {
                movieIdOld.getCommentList().remove(comment);
                movieIdOld = em.merge(movieIdOld);
            }
            if (movieIdNew != null && !movieIdNew.equals(movieIdOld)) {
                movieIdNew.getCommentList().add(comment);
                movieIdNew = em.merge(movieIdNew);
            }
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getCommentList().remove(comment);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getCommentList().add(comment);
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
                Integer id = comment.getId();
                if (findComment(id) == null) {
                    throw new NonexistentEntityException("The comment with id " + id + " no longer exists.");
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
            Comment comment;
            try {
                comment = em.getReference(Comment.class, id);
                comment.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The comment with id " + id + " no longer exists.", enfe);
            }
            Movie movieId = comment.getMovieId();
            if (movieId != null) {
                movieId.getCommentList().remove(comment);
                movieId = em.merge(movieId);
            }
            User userId = comment.getUserId();
            if (userId != null) {
                userId.getCommentList().remove(comment);
                userId = em.merge(userId);
            }
            em.remove(comment);
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

    public List<Comment> findCommentEntities() {
        return findCommentEntities(true, -1, -1);
    }

    public List<Comment> findCommentEntities(int maxResults, int firstResult) {
        return findCommentEntities(false, maxResults, firstResult);
    }

    private List<Comment> findCommentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Comment.class));
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

    public Comment findComment(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Comment.class, id);
        } finally {
            em.close();
        }
    }

    public int getCommentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Comment> rt = cq.from(Comment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
