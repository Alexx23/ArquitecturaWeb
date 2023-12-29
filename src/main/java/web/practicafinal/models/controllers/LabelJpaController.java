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
import java.util.Collection;
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
        if (label.getMovieCollection() == null) {
            label.setMovieCollection(new ArrayList<Movie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Movie> attachedMovieCollection = new ArrayList<Movie>();
            for (Movie movieCollectionMovieToAttach : label.getMovieCollection()) {
                movieCollectionMovieToAttach = em.getReference(movieCollectionMovieToAttach.getClass(), movieCollectionMovieToAttach.getId());
                attachedMovieCollection.add(movieCollectionMovieToAttach);
            }
            label.setMovieCollection(attachedMovieCollection);
            em.persist(label);
            for (Movie movieCollectionMovie : label.getMovieCollection()) {
                movieCollectionMovie.getLabelCollection().add(label);
                movieCollectionMovie = em.merge(movieCollectionMovie);
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
            Collection<Movie> movieCollectionOld = persistentLabel.getMovieCollection();
            Collection<Movie> movieCollectionNew = label.getMovieCollection();
            Collection<Movie> attachedMovieCollectionNew = new ArrayList<Movie>();
            for (Movie movieCollectionNewMovieToAttach : movieCollectionNew) {
                movieCollectionNewMovieToAttach = em.getReference(movieCollectionNewMovieToAttach.getClass(), movieCollectionNewMovieToAttach.getId());
                attachedMovieCollectionNew.add(movieCollectionNewMovieToAttach);
            }
            movieCollectionNew = attachedMovieCollectionNew;
            label.setMovieCollection(movieCollectionNew);
            label = em.merge(label);
            for (Movie movieCollectionOldMovie : movieCollectionOld) {
                if (!movieCollectionNew.contains(movieCollectionOldMovie)) {
                    movieCollectionOldMovie.getLabelCollection().remove(label);
                    movieCollectionOldMovie = em.merge(movieCollectionOldMovie);
                }
            }
            for (Movie movieCollectionNewMovie : movieCollectionNew) {
                if (!movieCollectionOld.contains(movieCollectionNewMovie)) {
                    movieCollectionNewMovie.getLabelCollection().add(label);
                    movieCollectionNewMovie = em.merge(movieCollectionNewMovie);
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
            Collection<Movie> movieCollection = label.getMovieCollection();
            for (Movie movieCollectionMovie : movieCollection) {
                movieCollectionMovie.getLabelCollection().remove(label);
                movieCollectionMovie = em.merge(movieCollectionMovie);
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
