package web.practicafinal.models.controllers;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.UserTransaction;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Alex
 */
public class ModelController {
    
    private static EntityManagerFactory emf = null;
    private static UserTransaction utx = null;
    
    private static ActorJpaController actor = null;
    private static AgeClassificationJpaController ageClassification = null;
    private static CardJpaController card = null;
    private static CommentJpaController comment = null;
    private static DirectorJpaController director = null;
    private static DistributorJpaController distributor = null;
    private static GenreJpaController genre = null;
    private static MovieJpaController movie = null;
    private static NationalityJpaController nationality = null;
    private static PaymentJpaController payment = null;
    private static RoleJpaController role = null;
    private static RoomJpaController room = null;
    private static SessionJpaController session = null;
    private static TicketJpaController ticket = null;
    private static UserJpaController user = null;
    
    public static void init() throws NamingException {
        emf = Persistence.createEntityManagerFactory("web_practicafinal_persistenceunit");
        utx = InitialContext.doLookup("java:comp/UserTransaction");
        
        actor = new ActorJpaController(utx, emf);
        ageClassification = new AgeClassificationJpaController(utx, emf);
        card = new CardJpaController(utx, emf);
        comment = new CommentJpaController(utx, emf);
        director = new DirectorJpaController(utx, emf);
        distributor = new DistributorJpaController(utx, emf);
        genre = new GenreJpaController(utx, emf);
        movie = new MovieJpaController(utx, emf);
        nationality = new NationalityJpaController(utx, emf);
        payment = new PaymentJpaController(utx, emf);
        role = new RoleJpaController(utx, emf);
        room = new RoomJpaController(utx, emf);
        session = new SessionJpaController(utx, emf);
        ticket = new TicketJpaController(utx, emf);
        user = new UserJpaController(utx, emf);
    }
    
    public static void destroy() throws NamingException {
        emf.close();
    }
    
    public static EntityManagerFactory getEMF() {
        return emf;
    }
    
    public static ActorJpaController getActor() {
        return actor;
    }
    
    public static AgeClassificationJpaController getAgeClassification() {
        return ageClassification;
    }
    
    public static CardJpaController getCard() {
        return card;
    }
    
    public static CommentJpaController getComment() {
        return comment;
    }

    public static DirectorJpaController getDirector() {
        return director;
    }

    public static DistributorJpaController getDistributor() {
        return distributor;
    }

    public static GenreJpaController getGenre() {
        return genre;
    }
    
    public static MovieJpaController getMovie() {
        return movie;
    }
    
    public static NationalityJpaController getNationality() {
        return nationality;
    }
    
    public static PaymentJpaController getPayment() {
        return payment;
    }
    
    public static RoleJpaController getRole() {
        return role;
    }
    
    public static RoomJpaController getRoom() {
        return room;
    }
    
    public static SessionJpaController getSession() {
        return session;
    }

    public static TicketJpaController getTicket() {
        return ticket;
    }
    
    public static UserJpaController getUser() {
        return user;
    }
    
}