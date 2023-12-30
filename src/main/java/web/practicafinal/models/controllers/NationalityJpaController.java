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
        if (nationality.getMovieList() == null) {
            nationality.setMovieList(new ArrayList<Movie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Movie> attachedMovieList = new ArrayList<Movie>();
            for (Movie movieListMovieToAttach : nationality.getMovieList()) {
                movieListMovieToAttach = em.getReference(movieListMovieToAttach.getClass(), movieListMovieToAttach.getId());
                attachedMovieList.add(movieListMovieToAttach);
            }
            nationality.setMovieList(attachedMovieList);
            em.persist(nationality);
            for (Movie movieListMovie : nationality.getMovieList()) {
                Nationality oldNationalityIdOfMovieListMovie = movieListMovie.getNationalityId();
                movieListMovie.setNationalityId(nationality);
                movieListMovie = em.merge(movieListMovie);
                if (oldNationalityIdOfMovieListMovie != null) {
                    oldNationalityIdOfMovieListMovie.getMovieList().remove(movieListMovie);
                    oldNationalityIdOfMovieListMovie = em.merge(oldNationalityIdOfMovieListMovie);
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
            List<Movie> movieListOld = persistentNationality.getMovieList();
            List<Movie> movieListNew = nationality.getMovieList();
            List<String> illegalOrphanMessages = null;
            for (Movie movieListOldMovie : movieListOld) {
                if (!movieListNew.contains(movieListOldMovie)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movie " + movieListOldMovie + " since its nationalityId field is not nullable.");
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
            nationality.setMovieList(movieListNew);
            nationality = em.merge(nationality);
            for (Movie movieListNewMovie : movieListNew) {
                if (!movieListOld.contains(movieListNewMovie)) {
                    Nationality oldNationalityIdOfMovieListNewMovie = movieListNewMovie.getNationalityId();
                    movieListNewMovie.setNationalityId(nationality);
                    movieListNewMovie = em.merge(movieListNewMovie);
                    if (oldNationalityIdOfMovieListNewMovie != null && !oldNationalityIdOfMovieListNewMovie.equals(nationality)) {
                        oldNationalityIdOfMovieListNewMovie.getMovieList().remove(movieListNewMovie);
                        oldNationalityIdOfMovieListNewMovie = em.merge(oldNationalityIdOfMovieListNewMovie);
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
            List<Movie> movieListOrphanCheck = nationality.getMovieList();
            for (Movie movieListOrphanCheckMovie : movieListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Nationality (" + nationality + ") cannot be destroyed since the Movie " + movieListOrphanCheckMovie + " in its movieList field has a non-nullable nationalityId field.");
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
