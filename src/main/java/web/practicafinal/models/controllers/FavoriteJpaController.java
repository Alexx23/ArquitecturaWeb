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
import web.practicafinal.models.Favorite;
import web.practicafinal.models.Movie;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class FavoriteJpaController implements Serializable {

    public FavoriteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Favorite favorite) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Movie movie = favorite.getMovie();
            if (movie != null) {
                movie = em.getReference(movie.getClass(), movie.getId());
                favorite.setMovie(movie);
            }
            User user = favorite.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getId());
                favorite.setUser(user);
            }
            em.persist(favorite);
            if (movie != null) {
                movie.getFavoriteList().add(favorite);
                movie = em.merge(movie);
            }
            if (user != null) {
                user.getFavoriteList().add(favorite);
                user = em.merge(user);
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

    public void edit(Favorite favorite) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Favorite persistentFavorite = em.find(Favorite.class, favorite.getId());
            Movie movieOld = persistentFavorite.getMovie();
            Movie movieNew = favorite.getMovie();
            User userOld = persistentFavorite.getUser();
            User userNew = favorite.getUser();
            if (movieNew != null) {
                movieNew = em.getReference(movieNew.getClass(), movieNew.getId());
                favorite.setMovie(movieNew);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getId());
                favorite.setUser(userNew);
            }
            favorite = em.merge(favorite);
            if (movieOld != null && !movieOld.equals(movieNew)) {
                movieOld.getFavoriteList().remove(favorite);
                movieOld = em.merge(movieOld);
            }
            if (movieNew != null && !movieNew.equals(movieOld)) {
                movieNew.getFavoriteList().add(favorite);
                movieNew = em.merge(movieNew);
            }
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getFavoriteList().remove(favorite);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getFavoriteList().add(favorite);
                userNew = em.merge(userNew);
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
                Integer id = favorite.getId();
                if (findFavorite(id) == null) {
                    throw new NonexistentEntityException("The favorite with id " + id + " no longer exists.");
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
            Favorite favorite;
            try {
                favorite = em.getReference(Favorite.class, id);
                favorite.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The favorite with id " + id + " no longer exists.", enfe);
            }
            Movie movie = favorite.getMovie();
            if (movie != null) {
                movie.getFavoriteList().remove(favorite);
                movie = em.merge(movie);
            }
            User user = favorite.getUser();
            if (user != null) {
                user.getFavoriteList().remove(favorite);
                user = em.merge(user);
            }
            em.remove(favorite);
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

    public List<Favorite> findFavoriteEntities() {
        return findFavoriteEntities(true, -1, -1);
    }

    public List<Favorite> findFavoriteEntities(int maxResults, int firstResult) {
        return findFavoriteEntities(false, maxResults, firstResult);
    }

    private List<Favorite> findFavoriteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Favorite.class));
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

    public Favorite findFavorite(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Favorite.class, id);
        } finally {
            em.close();
        }
    }

    public int getFavoriteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Favorite> rt = cq.from(Favorite.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
