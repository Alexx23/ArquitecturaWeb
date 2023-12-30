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
import java.util.ArrayList;
import java.util.List;
import web.practicafinal.models.Label;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class LabelJpaController implements Serializable {

    public LabelJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Label label) throws RollbackFailureException, Exception {
        if (label.getMovieList() == null) {
            label.setMovieList(new ArrayList<Movie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Movie> attachedMovieList = new ArrayList<Movie>();
            for (Movie movieListMovieToAttach : label.getMovieList()) {
                movieListMovieToAttach = em.getReference(movieListMovieToAttach.getClass(), movieListMovieToAttach.getId());
                attachedMovieList.add(movieListMovieToAttach);
            }
            label.setMovieList(attachedMovieList);
            em.persist(label);
            for (Movie movieListMovie : label.getMovieList()) {
                movieListMovie.getLabelList().add(label);
                movieListMovie = em.merge(movieListMovie);
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

    public void edit(Label label) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Label persistentLabel = em.find(Label.class, label.getId());
            List<Movie> movieListOld = persistentLabel.getMovieList();
            List<Movie> movieListNew = label.getMovieList();
            List<Movie> attachedMovieListNew = new ArrayList<Movie>();
            for (Movie movieListNewMovieToAttach : movieListNew) {
                movieListNewMovieToAttach = em.getReference(movieListNewMovieToAttach.getClass(), movieListNewMovieToAttach.getId());
                attachedMovieListNew.add(movieListNewMovieToAttach);
            }
            movieListNew = attachedMovieListNew;
            label.setMovieList(movieListNew);
            label = em.merge(label);
            for (Movie movieListOldMovie : movieListOld) {
                if (!movieListNew.contains(movieListOldMovie)) {
                    movieListOldMovie.getLabelList().remove(label);
                    movieListOldMovie = em.merge(movieListOldMovie);
                }
            }
            for (Movie movieListNewMovie : movieListNew) {
                if (!movieListOld.contains(movieListNewMovie)) {
                    movieListNewMovie.getLabelList().add(label);
                    movieListNewMovie = em.merge(movieListNewMovie);
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
                Integer id = label.getId();
                if (findLabel(id) == null) {
                    throw new NonexistentEntityException("The label with id " + id + " no longer exists.");
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
            Label label;
            try {
                label = em.getReference(Label.class, id);
                label.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The label with id " + id + " no longer exists.", enfe);
            }
            List<Movie> movieList = label.getMovieList();
            for (Movie movieListMovie : movieList) {
                movieListMovie.getLabelList().remove(label);
                movieListMovie = em.merge(movieListMovie);
            }
            em.remove(label);
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

    public List<Label> findLabelEntities() {
        return findLabelEntities(true, -1, -1);
    }

    public List<Label> findLabelEntities(int maxResults, int firstResult) {
        return findLabelEntities(false, maxResults, firstResult);
    }

    private List<Label> findLabelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Label.class));
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

    public Label findLabel(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Label.class, id);
        } finally {
            em.close();
        }
    }

    public int getLabelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Label> rt = cq.from(Label.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
