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
import java.util.List;
import web.practicafinal.models.Actor;
import web.practicafinal.models.Session;
import web.practicafinal.models.Comment;
import web.practicafinal.models.Movie;
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
        if (movie.getLabelList() == null) {
            movie.setLabelList(new ArrayList<Label>());
        }
        if (movie.getActorList() == null) {
            movie.setActorList(new ArrayList<Actor>());
        }
        if (movie.getSessionList() == null) {
            movie.setSessionList(new ArrayList<Session>());
        }
        if (movie.getCommentList() == null) {
            movie.setCommentList(new ArrayList<Comment>());
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
            List<Label> attachedLabelList = new ArrayList<Label>();
            for (Label labelListLabelToAttach : movie.getLabelList()) {
                labelListLabelToAttach = em.getReference(labelListLabelToAttach.getClass(), labelListLabelToAttach.getId());
                attachedLabelList.add(labelListLabelToAttach);
            }
            movie.setLabelList(attachedLabelList);
            List<Actor> attachedActorList = new ArrayList<Actor>();
            for (Actor actorListActorToAttach : movie.getActorList()) {
                actorListActorToAttach = em.getReference(actorListActorToAttach.getClass(), actorListActorToAttach.getId());
                attachedActorList.add(actorListActorToAttach);
            }
            movie.setActorList(attachedActorList);
            List<Session> attachedSessionList = new ArrayList<Session>();
            for (Session sessionListSessionToAttach : movie.getSessionList()) {
                sessionListSessionToAttach = em.getReference(sessionListSessionToAttach.getClass(), sessionListSessionToAttach.getId());
                attachedSessionList.add(sessionListSessionToAttach);
            }
            movie.setSessionList(attachedSessionList);
            List<Comment> attachedCommentList = new ArrayList<Comment>();
            for (Comment commentListCommentToAttach : movie.getCommentList()) {
                commentListCommentToAttach = em.getReference(commentListCommentToAttach.getClass(), commentListCommentToAttach.getId());
                attachedCommentList.add(commentListCommentToAttach);
            }
            movie.setCommentList(attachedCommentList);
            em.persist(movie);
            if (ageClassificationId != null) {
                ageClassificationId.getMovieList().add(movie);
                ageClassificationId = em.merge(ageClassificationId);
            }
            if (directorId != null) {
                directorId.getMovieList().add(movie);
                directorId = em.merge(directorId);
            }
            if (distributorId != null) {
                distributorId.getMovieList().add(movie);
                distributorId = em.merge(distributorId);
            }
            if (genreId != null) {
                genreId.getMovieList().add(movie);
                genreId = em.merge(genreId);
            }
            if (nationalityId != null) {
                nationalityId.getMovieList().add(movie);
                nationalityId = em.merge(nationalityId);
            }
            for (Label labelListLabel : movie.getLabelList()) {
                labelListLabel.getMovieList().add(movie);
                labelListLabel = em.merge(labelListLabel);
            }
            for (Actor actorListActor : movie.getActorList()) {
                actorListActor.getMovieList().add(movie);
                actorListActor = em.merge(actorListActor);
            }
            for (Session sessionListSession : movie.getSessionList()) {
                Movie oldMovieIdOfSessionListSession = sessionListSession.getMovieId();
                sessionListSession.setMovieId(movie);
                sessionListSession = em.merge(sessionListSession);
                if (oldMovieIdOfSessionListSession != null) {
                    oldMovieIdOfSessionListSession.getSessionList().remove(sessionListSession);
                    oldMovieIdOfSessionListSession = em.merge(oldMovieIdOfSessionListSession);
                }
            }
            for (Comment commentListComment : movie.getCommentList()) {
                Movie oldMovieIdOfCommentListComment = commentListComment.getMovieId();
                commentListComment.setMovieId(movie);
                commentListComment = em.merge(commentListComment);
                if (oldMovieIdOfCommentListComment != null) {
                    oldMovieIdOfCommentListComment.getCommentList().remove(commentListComment);
                    oldMovieIdOfCommentListComment = em.merge(oldMovieIdOfCommentListComment);
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
            List<Label> labelListOld = persistentMovie.getLabelList();
            List<Label> labelListNew = movie.getLabelList();
            List<Actor> actorListOld = persistentMovie.getActorList();
            List<Actor> actorListNew = movie.getActorList();
            List<Session> sessionListOld = persistentMovie.getSessionList();
            List<Session> sessionListNew = movie.getSessionList();
            List<Comment> commentListOld = persistentMovie.getCommentList();
            List<Comment> commentListNew = movie.getCommentList();
            List<String> illegalOrphanMessages = null;
            for (Session sessionListOldSession : sessionListOld) {
                if (!sessionListNew.contains(sessionListOldSession)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Session " + sessionListOldSession + " since its movieId field is not nullable.");
                }
            }
            for (Comment commentListOldComment : commentListOld) {
                if (!commentListNew.contains(commentListOldComment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comment " + commentListOldComment + " since its movieId field is not nullable.");
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
            List<Label> attachedLabelListNew = new ArrayList<Label>();
            for (Label labelListNewLabelToAttach : labelListNew) {
                labelListNewLabelToAttach = em.getReference(labelListNewLabelToAttach.getClass(), labelListNewLabelToAttach.getId());
                attachedLabelListNew.add(labelListNewLabelToAttach);
            }
            labelListNew = attachedLabelListNew;
            movie.setLabelList(labelListNew);
            List<Actor> attachedActorListNew = new ArrayList<Actor>();
            for (Actor actorListNewActorToAttach : actorListNew) {
                actorListNewActorToAttach = em.getReference(actorListNewActorToAttach.getClass(), actorListNewActorToAttach.getId());
                attachedActorListNew.add(actorListNewActorToAttach);
            }
            actorListNew = attachedActorListNew;
            movie.setActorList(actorListNew);
            List<Session> attachedSessionListNew = new ArrayList<Session>();
            for (Session sessionListNewSessionToAttach : sessionListNew) {
                sessionListNewSessionToAttach = em.getReference(sessionListNewSessionToAttach.getClass(), sessionListNewSessionToAttach.getId());
                attachedSessionListNew.add(sessionListNewSessionToAttach);
            }
            sessionListNew = attachedSessionListNew;
            movie.setSessionList(sessionListNew);
            List<Comment> attachedCommentListNew = new ArrayList<Comment>();
            for (Comment commentListNewCommentToAttach : commentListNew) {
                commentListNewCommentToAttach = em.getReference(commentListNewCommentToAttach.getClass(), commentListNewCommentToAttach.getId());
                attachedCommentListNew.add(commentListNewCommentToAttach);
            }
            commentListNew = attachedCommentListNew;
            movie.setCommentList(commentListNew);
            movie = em.merge(movie);
            if (ageClassificationIdOld != null && !ageClassificationIdOld.equals(ageClassificationIdNew)) {
                ageClassificationIdOld.getMovieList().remove(movie);
                ageClassificationIdOld = em.merge(ageClassificationIdOld);
            }
            if (ageClassificationIdNew != null && !ageClassificationIdNew.equals(ageClassificationIdOld)) {
                ageClassificationIdNew.getMovieList().add(movie);
                ageClassificationIdNew = em.merge(ageClassificationIdNew);
            }
            if (directorIdOld != null && !directorIdOld.equals(directorIdNew)) {
                directorIdOld.getMovieList().remove(movie);
                directorIdOld = em.merge(directorIdOld);
            }
            if (directorIdNew != null && !directorIdNew.equals(directorIdOld)) {
                directorIdNew.getMovieList().add(movie);
                directorIdNew = em.merge(directorIdNew);
            }
            if (distributorIdOld != null && !distributorIdOld.equals(distributorIdNew)) {
                distributorIdOld.getMovieList().remove(movie);
                distributorIdOld = em.merge(distributorIdOld);
            }
            if (distributorIdNew != null && !distributorIdNew.equals(distributorIdOld)) {
                distributorIdNew.getMovieList().add(movie);
                distributorIdNew = em.merge(distributorIdNew);
            }
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getMovieList().remove(movie);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getMovieList().add(movie);
                genreIdNew = em.merge(genreIdNew);
            }
            if (nationalityIdOld != null && !nationalityIdOld.equals(nationalityIdNew)) {
                nationalityIdOld.getMovieList().remove(movie);
                nationalityIdOld = em.merge(nationalityIdOld);
            }
            if (nationalityIdNew != null && !nationalityIdNew.equals(nationalityIdOld)) {
                nationalityIdNew.getMovieList().add(movie);
                nationalityIdNew = em.merge(nationalityIdNew);
            }
            for (Label labelListOldLabel : labelListOld) {
                if (!labelListNew.contains(labelListOldLabel)) {
                    labelListOldLabel.getMovieList().remove(movie);
                    labelListOldLabel = em.merge(labelListOldLabel);
                }
            }
            for (Label labelListNewLabel : labelListNew) {
                if (!labelListOld.contains(labelListNewLabel)) {
                    labelListNewLabel.getMovieList().add(movie);
                    labelListNewLabel = em.merge(labelListNewLabel);
                }
            }
            for (Actor actorListOldActor : actorListOld) {
                if (!actorListNew.contains(actorListOldActor)) {
                    actorListOldActor.getMovieList().remove(movie);
                    actorListOldActor = em.merge(actorListOldActor);
                }
            }
            for (Actor actorListNewActor : actorListNew) {
                if (!actorListOld.contains(actorListNewActor)) {
                    actorListNewActor.getMovieList().add(movie);
                    actorListNewActor = em.merge(actorListNewActor);
                }
            }
            for (Session sessionListNewSession : sessionListNew) {
                if (!sessionListOld.contains(sessionListNewSession)) {
                    Movie oldMovieIdOfSessionListNewSession = sessionListNewSession.getMovieId();
                    sessionListNewSession.setMovieId(movie);
                    sessionListNewSession = em.merge(sessionListNewSession);
                    if (oldMovieIdOfSessionListNewSession != null && !oldMovieIdOfSessionListNewSession.equals(movie)) {
                        oldMovieIdOfSessionListNewSession.getSessionList().remove(sessionListNewSession);
                        oldMovieIdOfSessionListNewSession = em.merge(oldMovieIdOfSessionListNewSession);
                    }
                }
            }
            for (Comment commentListNewComment : commentListNew) {
                if (!commentListOld.contains(commentListNewComment)) {
                    Movie oldMovieIdOfCommentListNewComment = commentListNewComment.getMovieId();
                    commentListNewComment.setMovieId(movie);
                    commentListNewComment = em.merge(commentListNewComment);
                    if (oldMovieIdOfCommentListNewComment != null && !oldMovieIdOfCommentListNewComment.equals(movie)) {
                        oldMovieIdOfCommentListNewComment.getCommentList().remove(commentListNewComment);
                        oldMovieIdOfCommentListNewComment = em.merge(oldMovieIdOfCommentListNewComment);
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
            List<Session> sessionListOrphanCheck = movie.getSessionList();
            for (Session sessionListOrphanCheckSession : sessionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Movie (" + movie + ") cannot be destroyed since the Session " + sessionListOrphanCheckSession + " in its sessionList field has a non-nullable movieId field.");
            }
            List<Comment> commentListOrphanCheck = movie.getCommentList();
            for (Comment commentListOrphanCheckComment : commentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Movie (" + movie + ") cannot be destroyed since the Comment " + commentListOrphanCheckComment + " in its commentList field has a non-nullable movieId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            AgeClassification ageClassificationId = movie.getAgeClassificationId();
            if (ageClassificationId != null) {
                ageClassificationId.getMovieList().remove(movie);
                ageClassificationId = em.merge(ageClassificationId);
            }
            Director directorId = movie.getDirectorId();
            if (directorId != null) {
                directorId.getMovieList().remove(movie);
                directorId = em.merge(directorId);
            }
            Distributor distributorId = movie.getDistributorId();
            if (distributorId != null) {
                distributorId.getMovieList().remove(movie);
                distributorId = em.merge(distributorId);
            }
            Genre genreId = movie.getGenreId();
            if (genreId != null) {
                genreId.getMovieList().remove(movie);
                genreId = em.merge(genreId);
            }
            Nationality nationalityId = movie.getNationalityId();
            if (nationalityId != null) {
                nationalityId.getMovieList().remove(movie);
                nationalityId = em.merge(nationalityId);
            }
            List<Label> labelList = movie.getLabelList();
            for (Label labelListLabel : labelList) {
                labelListLabel.getMovieList().remove(movie);
                labelListLabel = em.merge(labelListLabel);
            }
            List<Actor> actorList = movie.getActorList();
            for (Actor actorListActor : actorList) {
                actorListActor.getMovieList().remove(movie);
                actorListActor = em.merge(actorListActor);
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
