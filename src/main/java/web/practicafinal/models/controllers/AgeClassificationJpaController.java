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
        if (ageClassification.getMovieList() == null) {
            ageClassification.setMovieList(new ArrayList<Movie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Movie> attachedMovieList = new ArrayList<Movie>();
            for (Movie movieListMovieToAttach : ageClassification.getMovieList()) {
                movieListMovieToAttach = em.getReference(movieListMovieToAttach.getClass(), movieListMovieToAttach.getId());
                attachedMovieList.add(movieListMovieToAttach);
            }
            ageClassification.setMovieList(attachedMovieList);
            em.persist(ageClassification);
            for (Movie movieListMovie : ageClassification.getMovieList()) {
                AgeClassification oldAgeClassificationOfMovieListMovie = movieListMovie.getAgeClassification();
                movieListMovie.setAgeClassification(ageClassification);
                movieListMovie = em.merge(movieListMovie);
                if (oldAgeClassificationOfMovieListMovie != null) {
                    oldAgeClassificationOfMovieListMovie.getMovieList().remove(movieListMovie);
                    oldAgeClassificationOfMovieListMovie = em.merge(oldAgeClassificationOfMovieListMovie);
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
            List<Movie> movieListOld = persistentAgeClassification.getMovieList();
            List<Movie> movieListNew = ageClassification.getMovieList();
            List<String> illegalOrphanMessages = null;
            for (Movie movieListOldMovie : movieListOld) {
                if (!movieListNew.contains(movieListOldMovie)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movie " + movieListOldMovie + " since its ageClassification field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Movie> attachedMovieListNew = new ArrayList<Movie>();
            for (Movie movieListNewMovieToAttach : movieListNew) {
                movieListNewMovieToAttach = em.getReference(movieListNewMovieToAttach.getClass(), movieListNewMovieToAttach.getId());
                attachedMovieListNew.add(movieListNewMovieToAttach);
            }
            movieListNew = attachedMovieListNew;
            ageClassification.setMovieList(movieListNew);
            ageClassification = em.merge(ageClassification);
            for (Movie movieListNewMovie : movieListNew) {
                if (!movieListOld.contains(movieListNewMovie)) {
                    AgeClassification oldAgeClassificationOfMovieListNewMovie = movieListNewMovie.getAgeClassification();
                    movieListNewMovie.setAgeClassification(ageClassification);
                    movieListNewMovie = em.merge(movieListNewMovie);
                    if (oldAgeClassificationOfMovieListNewMovie != null && !oldAgeClassificationOfMovieListNewMovie.equals(ageClassification)) {
                        oldAgeClassificationOfMovieListNewMovie.getMovieList().remove(movieListNewMovie);
                        oldAgeClassificationOfMovieListNewMovie = em.merge(oldAgeClassificationOfMovieListNewMovie);
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
            List<Movie> movieListOrphanCheck = ageClassification.getMovieList();
            for (Movie movieListOrphanCheckMovie : movieListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This AgeClassification (" + ageClassification + ") cannot be destroyed since the Movie " + movieListOrphanCheckMovie + " in its movieList field has a non-nullable ageClassification field.");
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
