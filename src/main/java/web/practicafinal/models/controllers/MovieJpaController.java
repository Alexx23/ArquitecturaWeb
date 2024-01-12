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
import web.practicafinal.models.Actor;
import java.util.ArrayList;
import java.util.List;
import web.practicafinal.models.Session;
import web.practicafinal.models.Comment;
import web.practicafinal.models.Favorite;
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
        if (movie.getActorList() == null) {
            movie.setActorList(new ArrayList<Actor>());
        }
        if (movie.getSessionList() == null) {
            movie.setSessionList(new ArrayList<Session>());
        }
        if (movie.getCommentList() == null) {
            movie.setCommentList(new ArrayList<Comment>());
        }
        if (movie.getFavoriteList() == null) {
            movie.setFavoriteList(new ArrayList<Favorite>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            AgeClassification ageClassification = movie.getAgeClassification();
            if (ageClassification != null) {
                ageClassification = em.getReference(ageClassification.getClass(), ageClassification.getId());
                movie.setAgeClassification(ageClassification);
            }
            Director director = movie.getDirector();
            if (director != null) {
                director = em.getReference(director.getClass(), director.getId());
                movie.setDirector(director);
            }
            Distributor distributor = movie.getDistributor();
            if (distributor != null) {
                distributor = em.getReference(distributor.getClass(), distributor.getId());
                movie.setDistributor(distributor);
            }
            Genre genre = movie.getGenre();
            if (genre != null) {
                genre = em.getReference(genre.getClass(), genre.getId());
                movie.setGenre(genre);
            }
            Nationality nationality = movie.getNationality();
            if (nationality != null) {
                nationality = em.getReference(nationality.getClass(), nationality.getId());
                movie.setNationality(nationality);
            }
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
            List<Favorite> attachedFavoriteList = new ArrayList<Favorite>();
            for (Favorite favoriteListFavoriteToAttach : movie.getFavoriteList()) {
                favoriteListFavoriteToAttach = em.getReference(favoriteListFavoriteToAttach.getClass(), favoriteListFavoriteToAttach.getId());
                attachedFavoriteList.add(favoriteListFavoriteToAttach);
            }
            movie.setFavoriteList(attachedFavoriteList);
            em.persist(movie);
            if (ageClassification != null) {
                ageClassification.getMovieList().add(movie);
                ageClassification = em.merge(ageClassification);
            }
            if (director != null) {
                director.getMovieList().add(movie);
                director = em.merge(director);
            }
            if (distributor != null) {
                distributor.getMovieList().add(movie);
                distributor = em.merge(distributor);
            }
            if (genre != null) {
                genre.getMovieList().add(movie);
                genre = em.merge(genre);
            }
            if (nationality != null) {
                nationality.getMovieList().add(movie);
                nationality = em.merge(nationality);
            }
            for (Actor actorListActor : movie.getActorList()) {
                actorListActor.getMovieList().add(movie);
                actorListActor = em.merge(actorListActor);
            }
            for (Session sessionListSession : movie.getSessionList()) {
                Movie oldMovieOfSessionListSession = sessionListSession.getMovie();
                sessionListSession.setMovie(movie);
                sessionListSession = em.merge(sessionListSession);
                if (oldMovieOfSessionListSession != null) {
                    oldMovieOfSessionListSession.getSessionList().remove(sessionListSession);
                    oldMovieOfSessionListSession = em.merge(oldMovieOfSessionListSession);
                }
            }
            for (Comment commentListComment : movie.getCommentList()) {
                Movie oldMovieOfCommentListComment = commentListComment.getMovie();
                commentListComment.setMovie(movie);
                commentListComment = em.merge(commentListComment);
                if (oldMovieOfCommentListComment != null) {
                    oldMovieOfCommentListComment.getCommentList().remove(commentListComment);
                    oldMovieOfCommentListComment = em.merge(oldMovieOfCommentListComment);
                }
            }
            for (Favorite favoriteListFavorite : movie.getFavoriteList()) {
                Movie oldMovieOfFavoriteListFavorite = favoriteListFavorite.getMovie();
                favoriteListFavorite.setMovie(movie);
                favoriteListFavorite = em.merge(favoriteListFavorite);
                if (oldMovieOfFavoriteListFavorite != null) {
                    oldMovieOfFavoriteListFavorite.getFavoriteList().remove(favoriteListFavorite);
                    oldMovieOfFavoriteListFavorite = em.merge(oldMovieOfFavoriteListFavorite);
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
            AgeClassification ageClassificationOld = persistentMovie.getAgeClassification();
            AgeClassification ageClassificationNew = movie.getAgeClassification();
            Director directorOld = persistentMovie.getDirector();
            Director directorNew = movie.getDirector();
            Distributor distributorOld = persistentMovie.getDistributor();
            Distributor distributorNew = movie.getDistributor();
            Genre genreOld = persistentMovie.getGenre();
            Genre genreNew = movie.getGenre();
            Nationality nationalityOld = persistentMovie.getNationality();
            Nationality nationalityNew = movie.getNationality();
            List<Actor> actorListOld = persistentMovie.getActorList();
            List<Actor> actorListNew = movie.getActorList();
            List<Session> sessionListOld = persistentMovie.getSessionList();
            List<Session> sessionListNew = movie.getSessionList();
            List<Comment> commentListOld = persistentMovie.getCommentList();
            List<Comment> commentListNew = movie.getCommentList();
            List<Favorite> favoriteListOld = persistentMovie.getFavoriteList();
            List<Favorite> favoriteListNew = movie.getFavoriteList();
            List<String> illegalOrphanMessages = null;
            for (Session sessionListOldSession : sessionListOld) {
                if (!sessionListNew.contains(sessionListOldSession)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Session " + sessionListOldSession + " since its movie field is not nullable.");
                }
            }
            for (Comment commentListOldComment : commentListOld) {
                if (!commentListNew.contains(commentListOldComment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comment " + commentListOldComment + " since its movie field is not nullable.");
                }
            }
            for (Favorite favoriteListOldFavorite : favoriteListOld) {
                if (!favoriteListNew.contains(favoriteListOldFavorite)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Favorite " + favoriteListOldFavorite + " since its movie field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (ageClassificationNew != null) {
                ageClassificationNew = em.getReference(ageClassificationNew.getClass(), ageClassificationNew.getId());
                movie.setAgeClassification(ageClassificationNew);
            }
            if (directorNew != null) {
                directorNew = em.getReference(directorNew.getClass(), directorNew.getId());
                movie.setDirector(directorNew);
            }
            if (distributorNew != null) {
                distributorNew = em.getReference(distributorNew.getClass(), distributorNew.getId());
                movie.setDistributor(distributorNew);
            }
            if (genreNew != null) {
                genreNew = em.getReference(genreNew.getClass(), genreNew.getId());
                movie.setGenre(genreNew);
            }
            if (nationalityNew != null) {
                nationalityNew = em.getReference(nationalityNew.getClass(), nationalityNew.getId());
                movie.setNationality(nationalityNew);
            }
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
            List<Favorite> attachedFavoriteListNew = new ArrayList<Favorite>();
            for (Favorite favoriteListNewFavoriteToAttach : favoriteListNew) {
                favoriteListNewFavoriteToAttach = em.getReference(favoriteListNewFavoriteToAttach.getClass(), favoriteListNewFavoriteToAttach.getId());
                attachedFavoriteListNew.add(favoriteListNewFavoriteToAttach);
            }
            favoriteListNew = attachedFavoriteListNew;
            movie.setFavoriteList(favoriteListNew);
            movie = em.merge(movie);
            if (ageClassificationOld != null && !ageClassificationOld.equals(ageClassificationNew)) {
                ageClassificationOld.getMovieList().remove(movie);
                ageClassificationOld = em.merge(ageClassificationOld);
            }
            if (ageClassificationNew != null && !ageClassificationNew.equals(ageClassificationOld)) {
                ageClassificationNew.getMovieList().add(movie);
                ageClassificationNew = em.merge(ageClassificationNew);
            }
            if (directorOld != null && !directorOld.equals(directorNew)) {
                directorOld.getMovieList().remove(movie);
                directorOld = em.merge(directorOld);
            }
            if (directorNew != null && !directorNew.equals(directorOld)) {
                directorNew.getMovieList().add(movie);
                directorNew = em.merge(directorNew);
            }
            if (distributorOld != null && !distributorOld.equals(distributorNew)) {
                distributorOld.getMovieList().remove(movie);
                distributorOld = em.merge(distributorOld);
            }
            if (distributorNew != null && !distributorNew.equals(distributorOld)) {
                distributorNew.getMovieList().add(movie);
                distributorNew = em.merge(distributorNew);
            }
            if (genreOld != null && !genreOld.equals(genreNew)) {
                genreOld.getMovieList().remove(movie);
                genreOld = em.merge(genreOld);
            }
            if (genreNew != null && !genreNew.equals(genreOld)) {
                genreNew.getMovieList().add(movie);
                genreNew = em.merge(genreNew);
            }
            if (nationalityOld != null && !nationalityOld.equals(nationalityNew)) {
                nationalityOld.getMovieList().remove(movie);
                nationalityOld = em.merge(nationalityOld);
            }
            if (nationalityNew != null && !nationalityNew.equals(nationalityOld)) {
                nationalityNew.getMovieList().add(movie);
                nationalityNew = em.merge(nationalityNew);
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
                    Movie oldMovieOfSessionListNewSession = sessionListNewSession.getMovie();
                    sessionListNewSession.setMovie(movie);
                    sessionListNewSession = em.merge(sessionListNewSession);
                    if (oldMovieOfSessionListNewSession != null && !oldMovieOfSessionListNewSession.equals(movie)) {
                        oldMovieOfSessionListNewSession.getSessionList().remove(sessionListNewSession);
                        oldMovieOfSessionListNewSession = em.merge(oldMovieOfSessionListNewSession);
                    }
                }
            }
            for (Comment commentListNewComment : commentListNew) {
                if (!commentListOld.contains(commentListNewComment)) {
                    Movie oldMovieOfCommentListNewComment = commentListNewComment.getMovie();
                    commentListNewComment.setMovie(movie);
                    commentListNewComment = em.merge(commentListNewComment);
                    if (oldMovieOfCommentListNewComment != null && !oldMovieOfCommentListNewComment.equals(movie)) {
                        oldMovieOfCommentListNewComment.getCommentList().remove(commentListNewComment);
                        oldMovieOfCommentListNewComment = em.merge(oldMovieOfCommentListNewComment);
                    }
                }
            }
            for (Favorite favoriteListNewFavorite : favoriteListNew) {
                if (!favoriteListOld.contains(favoriteListNewFavorite)) {
                    Movie oldMovieOfFavoriteListNewFavorite = favoriteListNewFavorite.getMovie();
                    favoriteListNewFavorite.setMovie(movie);
                    favoriteListNewFavorite = em.merge(favoriteListNewFavorite);
                    if (oldMovieOfFavoriteListNewFavorite != null && !oldMovieOfFavoriteListNewFavorite.equals(movie)) {
                        oldMovieOfFavoriteListNewFavorite.getFavoriteList().remove(favoriteListNewFavorite);
                        oldMovieOfFavoriteListNewFavorite = em.merge(oldMovieOfFavoriteListNewFavorite);
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
                illegalOrphanMessages.add("This Movie (" + movie + ") cannot be destroyed since the Session " + sessionListOrphanCheckSession + " in its sessionList field has a non-nullable movie field.");
            }
            List<Comment> commentListOrphanCheck = movie.getCommentList();
            for (Comment commentListOrphanCheckComment : commentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Movie (" + movie + ") cannot be destroyed since the Comment " + commentListOrphanCheckComment + " in its commentList field has a non-nullable movie field.");
            }
            List<Favorite> favoriteListOrphanCheck = movie.getFavoriteList();
            for (Favorite favoriteListOrphanCheckFavorite : favoriteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Movie (" + movie + ") cannot be destroyed since the Favorite " + favoriteListOrphanCheckFavorite + " in its favoriteList field has a non-nullable movie field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            AgeClassification ageClassification = movie.getAgeClassification();
            if (ageClassification != null) {
                ageClassification.getMovieList().remove(movie);
                ageClassification = em.merge(ageClassification);
            }
            Director director = movie.getDirector();
            if (director != null) {
                director.getMovieList().remove(movie);
                director = em.merge(director);
            }
            Distributor distributor = movie.getDistributor();
            if (distributor != null) {
                distributor.getMovieList().remove(movie);
                distributor = em.merge(distributor);
            }
            Genre genre = movie.getGenre();
            if (genre != null) {
                genre.getMovieList().remove(movie);
                genre = em.merge(genre);
            }
            Nationality nationality = movie.getNationality();
            if (nationality != null) {
                nationality.getMovieList().remove(movie);
                nationality = em.merge(nationality);
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
