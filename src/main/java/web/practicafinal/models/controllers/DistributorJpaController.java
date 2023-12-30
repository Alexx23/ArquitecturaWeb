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
import web.practicafinal.models.Distributor;
import web.practicafinal.models.controllers.exceptions.IllegalOrphanException;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class DistributorJpaController implements Serializable {

    public DistributorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Distributor distributor) throws RollbackFailureException, Exception {
        if (distributor.getMovieList() == null) {
            distributor.setMovieList(new ArrayList<Movie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Movie> attachedMovieList = new ArrayList<Movie>();
            for (Movie movieListMovieToAttach : distributor.getMovieList()) {
                movieListMovieToAttach = em.getReference(movieListMovieToAttach.getClass(), movieListMovieToAttach.getId());
                attachedMovieList.add(movieListMovieToAttach);
            }
            distributor.setMovieList(attachedMovieList);
            em.persist(distributor);
            for (Movie movieListMovie : distributor.getMovieList()) {
                Distributor oldDistributorIdOfMovieListMovie = movieListMovie.getDistributorId();
                movieListMovie.setDistributorId(distributor);
                movieListMovie = em.merge(movieListMovie);
                if (oldDistributorIdOfMovieListMovie != null) {
                    oldDistributorIdOfMovieListMovie.getMovieList().remove(movieListMovie);
                    oldDistributorIdOfMovieListMovie = em.merge(oldDistributorIdOfMovieListMovie);
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

    public void edit(Distributor distributor) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Distributor persistentDistributor = em.find(Distributor.class, distributor.getId());
            List<Movie> movieListOld = persistentDistributor.getMovieList();
            List<Movie> movieListNew = distributor.getMovieList();
            List<String> illegalOrphanMessages = null;
            for (Movie movieListOldMovie : movieListOld) {
                if (!movieListNew.contains(movieListOldMovie)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movie " + movieListOldMovie + " since its distributorId field is not nullable.");
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
            distributor.setMovieList(movieListNew);
            distributor = em.merge(distributor);
            for (Movie movieListNewMovie : movieListNew) {
                if (!movieListOld.contains(movieListNewMovie)) {
                    Distributor oldDistributorIdOfMovieListNewMovie = movieListNewMovie.getDistributorId();
                    movieListNewMovie.setDistributorId(distributor);
                    movieListNewMovie = em.merge(movieListNewMovie);
                    if (oldDistributorIdOfMovieListNewMovie != null && !oldDistributorIdOfMovieListNewMovie.equals(distributor)) {
                        oldDistributorIdOfMovieListNewMovie.getMovieList().remove(movieListNewMovie);
                        oldDistributorIdOfMovieListNewMovie = em.merge(oldDistributorIdOfMovieListNewMovie);
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
                Integer id = distributor.getId();
                if (findDistributor(id) == null) {
                    throw new NonexistentEntityException("The distributor with id " + id + " no longer exists.");
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
            Distributor distributor;
            try {
                distributor = em.getReference(Distributor.class, id);
                distributor.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The distributor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Movie> movieListOrphanCheck = distributor.getMovieList();
            for (Movie movieListOrphanCheckMovie : movieListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Distributor (" + distributor + ") cannot be destroyed since the Movie " + movieListOrphanCheckMovie + " in its movieList field has a non-nullable distributorId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(distributor);
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

    public List<Distributor> findDistributorEntities() {
        return findDistributorEntities(true, -1, -1);
    }

    public List<Distributor> findDistributorEntities(int maxResults, int firstResult) {
        return findDistributorEntities(false, maxResults, firstResult);
    }

    private List<Distributor> findDistributorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Distributor.class));
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

    public Distributor findDistributor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Distributor.class, id);
        } finally {
            em.close();
        }
    }

    public int getDistributorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Distributor> rt = cq.from(Distributor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
