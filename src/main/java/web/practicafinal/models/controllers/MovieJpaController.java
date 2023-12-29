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
import web.practicafinal.models.AgeClassification;
import web.practicafinal.models.Director;
import web.practicafinal.models.Distributor;
import web.practicafinal.models.Genre;
import web.practicafinal.models.Nationality;
import web.practicafinal.models.Label;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import web.practicafinal.models.Actor;
import web.practicafinal.models.Movie;
import web.practicafinal.models.Session;
import web.practicafinal.models.controllers.exceptions.IllegalOrphanException;
import web.practicafinal.models.controllers.exceptions.NonexistentEntityException;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Alex
 */
public class MovieJpaController implements Serializable {

    public MovieJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movie movie) throws RollbackFailureException, Exception {
        if (movie.getLabelCollection() == null) {
            movie.setLabelCollection(new ArrayList<Label>());
        }
        if (movie.getActorCollection() == null) {
            movie.setActorCollection(new ArrayList<Actor>());
        }
        if (movie.getSessionCollection() == null) {
            movie.setSessionCollection(new ArrayList<Session>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            AgeClassification ageClassificationId = movie.getAgeClassificationId();
            if (ageClassificationId != null) {
                ageClassificationId = em.getReference(ageClassificationId.getClass(), ageClassificationId.getId());
                movie.setAgeClassificationId(ageClassificationId);
            }
            Director directorId = movie.getDirectorId();
            if (directorId != null) {
                directorId = em.getReference(directorId.getClass(), directorId.getId());
                movie.setDirectorId(directorId);
            }
            Distributor distributorId = movie.getDistributorId();
            if (distributorId != null) {
                distributorId = em.getReference(distributorId.getClass(), distributorId.getId());
                movie.setDistributorId(distributorId);
            }
            Genre genreId = movie.getGenreId();
            if (genreId != null) {
                genreId = em.getReference(genreId.getClass(), genreId.getId());
                movie.setGenreId(genreId);
            }
            Nationality nationalityId = movie.getNationalityId();
            if (nationalityId != null) {
                nationalityId = em.getReference(nationalityId.getClass(), nationalityId.getId());
                movie.setNationalityId(nationalityId);
            }
            Collection<Label> attachedLabelCollection = new ArrayList<Label>();
            for (Label labelCollectionLabelToAttach : movie.getLabelCollection()) {
                labelCollectionLabelToAttach = em.getReference(labelCollectionLabelToAttach.getClass(), labelCollectionLabelToAttach.getId());
                attachedLabelCollection.add(labelCollectionLabelToAttach);
            }
            movie.setLabelCollection(attachedLabelCollection);
            Collection<Actor> attachedActorCollection = new ArrayList<Actor>();
            for (Actor actorCollectionActorToAttach : movie.getActorCollection()) {
                actorCollectionActorToAttach = em.getReference(actorCollectionActorToAttach.getClass(), actorCollectionActorToAttach.getId());
                attachedActorCollection.add(actorCollectionActorToAttach);
            }
            movie.setActorCollection(attachedActorCollection);
            Collection<Session> attachedSessionCollection = new ArrayList<Session>();
            for (Session sessionCollectionSessionToAttach : movie.getSessionCollection()) {
                sessionCollectionSessionToAttach = em.getReference(sessionCollectionSessionToAttach.getClass(), sessionCollectionSessionToAttach.getId());
                attachedSessionCollection.add(sessionCollectionSessionToAttach);
            }
            movie.setSessionCollection(attachedSessionCollection);
            em.persist(movie);
            if (ageClassificationId != null) {
                ageClassificationId.getMovieCollection().add(movie);
                ageClassificationId = em.merge(ageClassificationId);
            }
            if (directorId != null) {
                directorId.getMovieCollection().add(movie);
                directorId = em.merge(directorId);
            }
            if (distributorId != null) {
                distributorId.getMovieCollection().add(movie);
                distributorId = em.merge(distributorId);
            }
            if (genreId != null) {
                genreId.getMovieCollection().add(movie);
                genreId = em.merge(genreId);
            }
            if (nationalityId != null) {
                nationalityId.getMovieCollection().add(movie);
                nationalityId = em.merge(nationalityId);
            }
            for (Label labelCollectionLabel : movie.getLabelCollection()) {
                labelCollectionLabel.getMovieCollection().add(movie);
                labelCollectionLabel = em.merge(labelCollectionLabel);
            }
            for (Actor actorCollectionActor : movie.getActorCollection()) {
                actorCollectionActor.getMovieCollection().add(movie);
                actorCollectionActor = em.merge(actorCollectionActor);
            }
            for (Session sessionCollectionSession : movie.getSessionCollection()) {
                Movie oldMovieIdOfSessionCollectionSession = sessionCollectionSession.getMovieId();
                sessionCollectionSession.setMovieId(movie);
                sessionCollectionSession = em.merge(sessionCollectionSession);
                if (oldMovieIdOfSessionCollectionSession != null) {
                    oldMovieIdOfSessionCollectionSession.getSessionCollection().remove(sessionCollectionSession);
                    oldMovieIdOfSessionCollectionSession = em.merge(oldMovieIdOfSessionCollectionSession);
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

    public void edit(Movie movie) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Movie persistentMovie = em.find(Movie.class, movie.getId());
            AgeClassification ageClassificationIdOld = persistentMovie.getAgeClassificationId();
            AgeClassification ageClassificationIdNew = movie.getAgeClassificationId();
            Director directorIdOld = persistentMovie.getDirectorId();
            Director directorIdNew = movie.getDirectorId();
            Distributor distributorIdOld = persistentMovie.getDistributorId();
            Distributor distributorIdNew = movie.getDistributorId();
            Genre genreIdOld = persistentMovie.getGenreId();
            Genre genreIdNew = movie.getGenreId();
            Nationality nationalityIdOld = persistentMovie.getNationalityId();
            Nationality nationalityIdNew = movie.getNationalityId();
            Collection<Label> labelCollectionOld = persistentMovie.getLabelCollection();
            Collection<Label> labelCollectionNew = movie.getLabelCollection();
            Collection<Actor> actorCollectionOld = persistentMovie.getActorCollection();
            Collection<Actor> actorCollectionNew = movie.getActorCollection();
            Collection<Session> sessionCollectionOld = persistentMovie.getSessionCollection();
            Collection<Session> sessionCollectionNew = movie.getSessionCollection();
            List<String> illegalOrphanMessages = null;
            for (Session sessionCollectionOldSession : sessionCollectionOld) {
                if (!sessionCollectionNew.contains(sessionCollectionOldSession)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Session " + sessionCollectionOldSession + " since its movieId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (ageClassificationIdNew != null) {
                ageClassificationIdNew = em.getReference(ageClassificationIdNew.getClass(), ageClassificationIdNew.getId());
                movie.setAgeClassificationId(ageClassificationIdNew);
            }
            if (directorIdNew != null) {
                directorIdNew = em.getReference(directorIdNew.getClass(), directorIdNew.getId());
                movie.setDirectorId(directorIdNew);
            }
            if (distributorIdNew != null) {
                distributorIdNew = em.getReference(distributorIdNew.getClass(), distributorIdNew.getId());
                movie.setDistributorId(distributorIdNew);
            }
            if (genreIdNew != null) {
                genreIdNew = em.getReference(genreIdNew.getClass(), genreIdNew.getId());
                movie.setGenreId(genreIdNew);
            }
            if (nationalityIdNew != null) {
                nationalityIdNew = em.getReference(nationalityIdNew.getClass(), nationalityIdNew.getId());
                movie.setNationalityId(nationalityIdNew);
            }
            Collection<Label> attachedLabelCollectionNew = new ArrayList<Label>();
            for (Label labelCollectionNewLabelToAttach : labelCollectionNew) {
                labelCollectionNewLabelToAttach = em.getReference(labelCollectionNewLabelToAttach.getClass(), labelCollectionNewLabelToAttach.getId());
                attachedLabelCollectionNew.add(labelCollectionNewLabelToAttach);
            }
            labelCollectionNew = attachedLabelCollectionNew;
            movie.setLabelCollection(labelCollectionNew);
            Collection<Actor> attachedActorCollectionNew = new ArrayList<Actor>();
            for (Actor actorCollectionNewActorToAttach : actorCollectionNew) {
                actorCollectionNewActorToAttach = em.getReference(actorCollectionNewActorToAttach.getClass(), actorCollectionNewActorToAttach.getId());
                attachedActorCollectionNew.add(actorCollectionNewActorToAttach);
            }
            actorCollectionNew = attachedActorCollectionNew;
            movie.setActorCollection(actorCollectionNew);
            Collection<Session> attachedSessionCollectionNew = new ArrayList<Session>();
            for (Session sessionCollectionNewSessionToAttach : sessionCollectionNew) {
                sessionCollectionNewSessionToAttach = em.getReference(sessionCollectionNewSessionToAttach.getClass(), sessionCollectionNewSessionToAttach.getId());
                attachedSessionCollectionNew.add(sessionCollectionNewSessionToAttach);
            }
            sessionCollectionNew = attachedSessionCollectionNew;
            movie.setSessionCollection(sessionCollectionNew);
            movie = em.merge(movie);
            if (ageClassificationIdOld != null && !ageClassificationIdOld.equals(ageClassificationIdNew)) {
                ageClassificationIdOld.getMovieCollection().remove(movie);
                ageClassificationIdOld = em.merge(ageClassificationIdOld);
            }
            if (ageClassificationIdNew != null && !ageClassificationIdNew.equals(ageClassificationIdOld)) {
                ageClassificationIdNew.getMovieCollection().add(movie);
                ageClassificationIdNew = em.merge(ageClassificationIdNew);
            }
            if (directorIdOld != null && !directorIdOld.equals(directorIdNew)) {
                directorIdOld.getMovieCollection().remove(movie);
                directorIdOld = em.merge(directorIdOld);
            }
            if (directorIdNew != null && !directorIdNew.equals(directorIdOld)) {
                directorIdNew.getMovieCollection().add(movie);
                directorIdNew = em.merge(directorIdNew);
            }
            if (distributorIdOld != null && !distributorIdOld.equals(distributorIdNew)) {
                distributorIdOld.getMovieCollection().remove(movie);
                distributorIdOld = em.merge(distributorIdOld);
            }
            if (distributorIdNew != null && !distributorIdNew.equals(distributorIdOld)) {
                distributorIdNew.getMovieCollection().add(movie);
                distributorIdNew = em.merge(distributorIdNew);
            }
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getMovieCollection().remove(movie);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getMovieCollection().add(movie);
                genreIdNew = em.merge(genreIdNew);
            }
            if (nationalityIdOld != null && !nationalityIdOld.equals(nationalityIdNew)) {
                nationalityIdOld.getMovieCollection().remove(movie);
                nationalityIdOld = em.merge(nationalityIdOld);
            }
            if (nationalityIdNew != null && !nationalityIdNew.equals(nationalityIdOld)) {
                nationalityIdNew.getMovieCollection().add(movie);
                nationalityIdNew = em.merge(nationalityIdNew);
            }
            for (Label labelCollectionOldLabel : labelCollectionOld) {
                if (!labelCollectionNew.contains(labelCollectionOldLabel)) {
                    labelCollectionOldLabel.getMovieCollection().remove(movie);
                    labelCollectionOldLabel = em.merge(labelCollectionOldLabel);
                }
            }
            for (Label labelCollectionNewLabel : labelCollectionNew) {
                if (!labelCollectionOld.contains(labelCollectionNewLabel)) {
                    labelCollectionNewLabel.getMovieCollection().add(movie);
                    labelCollectionNewLabel = em.merge(labelCollectionNewLabel);
                }
            }
            for (Actor actorCollectionOldActor : actorCollectionOld) {
                if (!actorCollectionNew.contains(actorCollectionOldActor)) {
                    actorCollectionOldActor.getMovieCollection().remove(movie);
                    actorCollectionOldActor = em.merge(actorCollectionOldActor);
                }
            }
            for (Actor actorCollectionNewActor : actorCollectionNew) {
                if (!actorCollectionOld.contains(actorCollectionNewActor)) {
                    actorCollectionNewActor.getMovieCollection().add(movie);
                    actorCollectionNewActor = em.merge(actorCollectionNewActor);
                }
            }
            for (Session sessionCollectionNewSession : sessionCollectionNew) {
                if (!sessionCollectionOld.contains(sessionCollectionNewSession)) {
                    Movie oldMovieIdOfSessionCollectionNewSession = sessionCollectionNewSession.getMovieId();
                    sessionCollectionNewSession.setMovieId(movie);
                    sessionCollectionNewSession = em.merge(sessionCollectionNewSession);
                    if (oldMovieIdOfSessionCollectionNewSession != null && !oldMovieIdOfSessionCollectionNewSession.equals(movie)) {
                        oldMovieIdOfSessionCollectionNewSession.getSessionCollection().remove(sessionCollectionNewSession);
                        oldMovieIdOfSessionCollectionNewSession = em.merge(oldMovieIdOfSessionCollectionNewSession);
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
                Integer id = movie.getId();
                if (findMovie(id) == null) {
                    throw new NonexistentEntityException("The movie with id " + id + " no longer exists.");
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
            Movie movie;
            try {
                movie = em.getReference(Movie.class, id);
                movie.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movie with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Session> sessionCollectionOrphanCheck = movie.getSessionCollection();
            for (Session sessionCollectionOrphanCheckSession : sessionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Movie (" + movie + ") cannot be destroyed since the Session " + sessionCollectionOrphanCheckSession + " in its sessionCollection field has a non-nullable movieId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            AgeClassification ageClassificationId = movie.getAgeClassificationId();
            if (ageClassificationId != null) {
                ageClassificationId.getMovieCollection().remove(movie);
                ageClassificationId = em.merge(ageClassificationId);
            }
            Director directorId = movie.getDirectorId();
            if (directorId != null) {
                directorId.getMovieCollection().remove(movie);
                directorId = em.merge(directorId);
            }
            Distributor distributorId = movie.getDistributorId();
            if (distributorId != null) {
                distributorId.getMovieCollection().remove(movie);
                distributorId = em.merge(distributorId);
            }
            Genre genreId = movie.getGenreId();
            if (genreId != null) {
                genreId.getMovieCollection().remove(movie);
                genreId = em.merge(genreId);
            }
            Nationality nationalityId = movie.getNationalityId();
            if (nationalityId != null) {
                nationalityId.getMovieCollection().remove(movie);
                nationalityId = em.merge(nationalityId);
            }
            Collection<Label> labelCollection = movie.getLabelCollection();
            for (Label labelCollectionLabel : labelCollection) {
                labelCollectionLabel.getMovieCollection().remove(movie);
                labelCollectionLabel = em.merge(labelCollectionLabel);
            }
            Collection<Actor> actorCollection = movie.getActorCollection();
            for (Actor actorCollectionActor : actorCollection) {
                actorCollectionActor.getMovieCollection().remove(movie);
                actorCollectionActor = em.merge(actorCollectionActor);
            }
            em.remove(movie);
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

    public List<Movie> findMovieEntities() {
        return findMovieEntities(true, -1, -1);
    }

    public List<Movie> findMovieEntities(int maxResults, int firstResult) {
        return findMovieEntities(false, maxResults, firstResult);
    }

    private List<Movie> findMovieEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Movie.class));
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

    public Movie findMovie(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movie.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovieCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Movie> rt = cq.from(Movie.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
