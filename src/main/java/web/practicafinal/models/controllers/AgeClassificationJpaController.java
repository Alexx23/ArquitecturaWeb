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
import web.practicafinal.models.AgeClassification;
import web.practicafinal.models.controllers.exceptions.IllegalOrphanException;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class AgeClassificationJpaController implements Serializable {

    public AgeClassificationJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AgeClassification ageClassification) throws RollbackFailureException, Exception {
        if (ageClassification.getMovieCollection() == null) {
            ageClassification.setMovieCollection(new ArrayList<Movie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Movie> attachedMovieCollection = new ArrayList<Movie>();
            for (Movie movieCollectionMovieToAttach : ageClassification.getMovieCollection()) {
                movieCollectionMovieToAttach = em.getReference(movieCollectionMovieToAttach.getClass(), movieCollectionMovieToAttach.getId());
                attachedMovieCollection.add(movieCollectionMovieToAttach);
            }
            ageClassification.setMovieCollection(attachedMovieCollection);
            em.persist(ageClassification);
            for (Movie movieCollectionMovie : ageClassification.getMovieCollection()) {
                AgeClassification oldAgeClassificationIdOfMovieCollectionMovie = movieCollectionMovie.getAgeClassificationId();
                movieCollectionMovie.setAgeClassificationId(ageClassification);
                movieCollectionMovie = em.merge(movieCollectionMovie);
                if (oldAgeClassificationIdOfMovieCollectionMovie != null) {
                    oldAgeClassificationIdOfMovieCollectionMovie.getMovieCollection().remove(movieCollectionMovie);
                    oldAgeClassificationIdOfMovieCollectionMovie = em.merge(oldAgeClassificationIdOfMovieCollectionMovie);
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

    public void edit(AgeClassification ageClassification) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            AgeClassification persistentAgeClassification = em.find(AgeClassification.class, ageClassification.getId());
            Collection<Movie> movieCollectionOld = persistentAgeClassification.getMovieCollection();
            Collection<Movie> movieCollectionNew = ageClassification.getMovieCollection();
            List<String> illegalOrphanMessages = null;
            for (Movie movieCollectionOldMovie : movieCollectionOld) {
                if (!movieCollectionNew.contains(movieCollectionOldMovie)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movie " + movieCollectionOldMovie + " since its ageClassificationId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Movie> attachedMovieCollectionNew = new ArrayList<Movie>();
            for (Movie movieCollectionNewMovieToAttach : movieCollectionNew) {
                movieCollectionNewMovieToAttach = em.getReference(movieCollectionNewMovieToAttach.getClass(), movieCollectionNewMovieToAttach.getId());
                attachedMovieCollectionNew.add(movieCollectionNewMovieToAttach);
            }
            movieCollectionNew = attachedMovieCollectionNew;
            ageClassification.setMovieCollection(movieCollectionNew);
            ageClassification = em.merge(ageClassification);
            for (Movie movieCollectionNewMovie : movieCollectionNew) {
                if (!movieCollectionOld.contains(movieCollectionNewMovie)) {
                    AgeClassification oldAgeClassificationIdOfMovieCollectionNewMovie = movieCollectionNewMovie.getAgeClassificationId();
                    movieCollectionNewMovie.setAgeClassificationId(ageClassification);
                    movieCollectionNewMovie = em.merge(movieCollectionNewMovie);
                    if (oldAgeClassificationIdOfMovieCollectionNewMovie != null && !oldAgeClassificationIdOfMovieCollectionNewMovie.equals(ageClassification)) {
                        oldAgeClassificationIdOfMovieCollectionNewMovie.getMovieCollection().remove(movieCollectionNewMovie);
                        oldAgeClassificationIdOfMovieCollectionNewMovie = em.merge(oldAgeClassificationIdOfMovieCollectionNewMovie);
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
                Integer id = ageClassification.getId();
                if (findAgeClassification(id) == null) {
                    throw new NonexistentEntityException("The ageClassification with id " + id + " no longer exists.");
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
            AgeClassification ageClassification;
            try {
                ageClassification = em.getReference(AgeClassification.class, id);
                ageClassification.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ageClassification with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Movie> movieCollectionOrphanCheck = ageClassification.getMovieCollection();
            for (Movie movieCollectionOrphanCheckMovie : movieCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This AgeClassification (" + ageClassification + ") cannot be destroyed since the Movie " + movieCollectionOrphanCheckMovie + " in its movieCollection field has a non-nullable ageClassificationId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(ageClassification);
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

    public List<AgeClassification> findAgeClassificationEntities() {
        return findAgeClassificationEntities(true, -1, -1);
    }

    public List<AgeClassification> findAgeClassificationEntities(int maxResults, int firstResult) {
        return findAgeClassificationEntities(false, maxResults, firstResult);
    }

    private List<AgeClassification> findAgeClassificationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AgeClassification.class));
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

    public AgeClassification findAgeClassification(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AgeClassification.class, id);
        } finally {
            em.close();
        }
    }

    public int getAgeClassificationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AgeClassification> rt = cq.from(AgeClassification.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
