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
import web.practicafinal.models.Director;
import web.practicafinal.models.controllers.exceptions.IllegalOrphanException;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class DirectorJpaController implements Serializable {

    public DirectorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Director director) throws RollbackFailureException, Exception {
        if (director.getMovieCollection() == null) {
            director.setMovieCollection(new ArrayList<Movie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Movie> attachedMovieCollection = new ArrayList<Movie>();
            for (Movie movieCollectionMovieToAttach : director.getMovieCollection()) {
                movieCollectionMovieToAttach = em.getReference(movieCollectionMovieToAttach.getClass(), movieCollectionMovieToAttach.getId());
                attachedMovieCollection.add(movieCollectionMovieToAttach);
            }
            director.setMovieCollection(attachedMovieCollection);
            em.persist(director);
            for (Movie movieCollectionMovie : director.getMovieCollection()) {
                Director oldDirectorIdOfMovieCollectionMovie = movieCollectionMovie.getDirectorId();
                movieCollectionMovie.setDirectorId(director);
                movieCollectionMovie = em.merge(movieCollectionMovie);
                if (oldDirectorIdOfMovieCollectionMovie != null) {
                    oldDirectorIdOfMovieCollectionMovie.getMovieCollection().remove(movieCollectionMovie);
                    oldDirectorIdOfMovieCollectionMovie = em.merge(oldDirectorIdOfMovieCollectionMovie);
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

    public void edit(Director director) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Director persistentDirector = em.find(Director.class, director.getId());
            Collection<Movie> movieCollectionOld = persistentDirector.getMovieCollection();
            Collection<Movie> movieCollectionNew = director.getMovieCollection();
            List<String> illegalOrphanMessages = null;
            for (Movie movieCollectionOldMovie : movieCollectionOld) {
                if (!movieCollectionNew.contains(movieCollectionOldMovie)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movie " + movieCollectionOldMovie + " since its directorId field is not nullable.");
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
            director.setMovieCollection(movieCollectionNew);
            director = em.merge(director);
            for (Movie movieCollectionNewMovie : movieCollectionNew) {
                if (!movieCollectionOld.contains(movieCollectionNewMovie)) {
                    Director oldDirectorIdOfMovieCollectionNewMovie = movieCollectionNewMovie.getDirectorId();
                    movieCollectionNewMovie.setDirectorId(director);
                    movieCollectionNewMovie = em.merge(movieCollectionNewMovie);
                    if (oldDirectorIdOfMovieCollectionNewMovie != null && !oldDirectorIdOfMovieCollectionNewMovie.equals(director)) {
                        oldDirectorIdOfMovieCollectionNewMovie.getMovieCollection().remove(movieCollectionNewMovie);
                        oldDirectorIdOfMovieCollectionNewMovie = em.merge(oldDirectorIdOfMovieCollectionNewMovie);
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
                Integer id = director.getId();
                if (findDirector(id) == null) {
                    throw new NonexistentEntityException("The director with id " + id + " no longer exists.");
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
            Director director;
            try {
                director = em.getReference(Director.class, id);
                director.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The director with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Movie> movieCollectionOrphanCheck = director.getMovieCollection();
            for (Movie movieCollectionOrphanCheckMovie : movieCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Director (" + director + ") cannot be destroyed since the Movie " + movieCollectionOrphanCheckMovie + " in its movieCollection field has a non-nullable directorId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(director);
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

    public List<Director> findDirectorEntities() {
        return findDirectorEntities(true, -1, -1);
    }

    public List<Director> findDirectorEntities(int maxResults, int firstResult) {
        return findDirectorEntities(false, maxResults, firstResult);
    }

    private List<Director> findDirectorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Director.class));
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

    public Director findDirector(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Director.class, id);
        } finally {
            em.close();
        }
    }

    public int getDirectorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Director> rt = cq.from(Director.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
