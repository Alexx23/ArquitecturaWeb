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
import web.practicafinal.models.Nationality;
import web.practicafinal.models.controllers.exceptions.IllegalOrphanException;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class NationalityJpaController implements Serializable {

    public NationalityJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Nationality nationality) throws RollbackFailureException, Exception {
        if (nationality.getMovieCollection() == null) {
            nationality.setMovieCollection(new ArrayList<Movie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Movie> attachedMovieCollection = new ArrayList<Movie>();
            for (Movie movieCollectionMovieToAttach : nationality.getMovieCollection()) {
                movieCollectionMovieToAttach = em.getReference(movieCollectionMovieToAttach.getClass(), movieCollectionMovieToAttach.getId());
                attachedMovieCollection.add(movieCollectionMovieToAttach);
            }
            nationality.setMovieCollection(attachedMovieCollection);
            em.persist(nationality);
            for (Movie movieCollectionMovie : nationality.getMovieCollection()) {
                Nationality oldNationalityIdOfMovieCollectionMovie = movieCollectionMovie.getNationalityId();
                movieCollectionMovie.setNationalityId(nationality);
                movieCollectionMovie = em.merge(movieCollectionMovie);
                if (oldNationalityIdOfMovieCollectionMovie != null) {
                    oldNationalityIdOfMovieCollectionMovie.getMovieCollection().remove(movieCollectionMovie);
                    oldNationalityIdOfMovieCollectionMovie = em.merge(oldNationalityIdOfMovieCollectionMovie);
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

    public void edit(Nationality nationality) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Nationality persistentNationality = em.find(Nationality.class, nationality.getId());
            Collection<Movie> movieCollectionOld = persistentNationality.getMovieCollection();
            Collection<Movie> movieCollectionNew = nationality.getMovieCollection();
            List<String> illegalOrphanMessages = null;
            for (Movie movieCollectionOldMovie : movieCollectionOld) {
                if (!movieCollectionNew.contains(movieCollectionOldMovie)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movie " + movieCollectionOldMovie + " since its nationalityId field is not nullable.");
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
            nationality.setMovieCollection(movieCollectionNew);
            nationality = em.merge(nationality);
            for (Movie movieCollectionNewMovie : movieCollectionNew) {
                if (!movieCollectionOld.contains(movieCollectionNewMovie)) {
                    Nationality oldNationalityIdOfMovieCollectionNewMovie = movieCollectionNewMovie.getNationalityId();
                    movieCollectionNewMovie.setNationalityId(nationality);
                    movieCollectionNewMovie = em.merge(movieCollectionNewMovie);
                    if (oldNationalityIdOfMovieCollectionNewMovie != null && !oldNationalityIdOfMovieCollectionNewMovie.equals(nationality)) {
                        oldNationalityIdOfMovieCollectionNewMovie.getMovieCollection().remove(movieCollectionNewMovie);
                        oldNationalityIdOfMovieCollectionNewMovie = em.merge(oldNationalityIdOfMovieCollectionNewMovie);
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
                Integer id = nationality.getId();
                if (findNationality(id) == null) {
                    throw new NonexistentEntityException("The nationality with id " + id + " no longer exists.");
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
            Nationality nationality;
            try {
                nationality = em.getReference(Nationality.class, id);
                nationality.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The nationality with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Movie> movieCollectionOrphanCheck = nationality.getMovieCollection();
            for (Movie movieCollectionOrphanCheckMovie : movieCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Nationality (" + nationality + ") cannot be destroyed since the Movie " + movieCollectionOrphanCheckMovie + " in its movieCollection field has a non-nullable nationalityId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(nationality);
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

    public List<Nationality> findNationalityEntities() {
        return findNationalityEntities(true, -1, -1);
    }

    public List<Nationality> findNationalityEntities(int maxResults, int firstResult) {
        return findNationalityEntities(false, maxResults, firstResult);
    }

    private List<Nationality> findNationalityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Nationality.class));
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

    public Nationality findNationality(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Nationality.class, id);
        } finally {
            em.close();
        }
    }

    public int getNationalityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Nationality> rt = cq.from(Nationality.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
