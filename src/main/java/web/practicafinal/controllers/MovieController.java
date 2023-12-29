package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import web.practicafinal.enums.RequestScope;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.AgeClassification;
import web.practicafinal.models.Director;
import web.practicafinal.models.Distributor;
import web.practicafinal.models.Genre;
import web.practicafinal.models.Movie;
import web.practicafinal.models.Nationality;
import web.practicafinal.models.controllers.AgeClassificationJpaController;
import web.practicafinal.models.controllers.DirectorJpaController;
import web.practicafinal.models.controllers.DistributorJpaController;
import web.practicafinal.models.controllers.GenreJpaController;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.MovieJpaController;
import web.practicafinal.models.controllers.NationalityJpaController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.JsonUtils;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
@WebServlet("/movie/*")
public class MovieController extends HttpServlet {
    
    private static MovieJpaController movieJpaController = null;
    private static AgeClassificationJpaController ageClassificationJpaController = null;
    private static DirectorJpaController directorJpaController = null;
    private static DistributorJpaController distributorJpaController = null;
    private static GenreJpaController genreJpaController = null;
    private static NationalityJpaController nationalityJpaController = null;

    public MovieController() {
        super();
    }
    
    @Override
    public void init() {
        movieJpaController = ModelController.getMovie();
        ageClassificationJpaController = ModelController.getAgeClassification();
        directorJpaController = ModelController.getDirector();
        distributorJpaController = ModelController.getDistributor();
        genreJpaController = ModelController.getGenre();
        nationalityJpaController = ModelController.getNationality();
    }

    /*
    /movie -> Ver lista con todas las peliculas
    /movie/{id} -> Ver información de la película con id = {id}
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String movieIdStr = Request.getURLValue(request);
        
        if (movieIdStr == null) {
            List<Movie> movies = movieJpaController.findMovieEntities();
            Response.outputData(response, 200, movies, true);
            return;
        }
        
        int movieId = Integer.parseInt(movieIdStr);
        Movie movie = movieJpaController.findMovie(movieId);
        if (movie == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la película solicitada");
            return;
        }
        Response.outputData(response, 200, movie, true);
        
    }
    
    /*
    /movie -> Crear pelicula
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Movie movie = validateMovie(request, response);
           
            try {
                movieJpaController.create(movie);
                Response.outputData(response, 200, movie, true);
            } catch (Exception ex) {
                CustomLogger.errorThrow(MovieController.class.getName(), ex);
            }
        
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
    }
    
    
    /*
    /movie/{id} -> Actualizar pelicula con id = {id}
    */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int movieId = Integer.parseInt(Request.getURLValue(request));
        
        try {
            Movie movie = validateMovie(request, response);
            movie.setId(movieId);
           
            try {
                movieJpaController.edit(movie);
            } catch (Exception ex) {
                CustomLogger.errorThrow(MovieController.class.getName(), ex);
            }
        
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
    }
    
    /*
    /movie/{id} -> Eliminar película con id = {id}
    */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int movieId = Integer.parseInt(Request.getURLValue(request));
        
        try {
            movieJpaController.destroy(movieId);
            Response.outputMessage(response, 200, "Película eliminada correctamente");
        } catch (RollbackFailureException ex) {
            CustomLogger.errorThrow(MovieController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar la película");
        } catch (Exception ex) {
            CustomLogger.errorThrow(MovieController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar la película");
        }
        
    }
    
    
    private Movie validateMovie(HttpServletRequest request, HttpServletResponse response) throws ValidateException {
            Map<String, String> validatedRequest = Request.validate(RequestScope.CREATE_MOVIE, request, 
                    "name", "web", "original_title", "duration", "year", "genre_id", "nationality_id", "distributor_id", "director_id", "age_classification_id");
        
            short duration = Short.valueOf(validatedRequest.get("duration"));
            short year = Short.valueOf(validatedRequest.get("year"));
            int genreId = Integer.valueOf(validatedRequest.get("genre_id"));
            int nationalityId = Integer.valueOf(validatedRequest.get("nationality_id"));
            int distributorId = Integer.valueOf(validatedRequest.get("distributor_id"));
            int directorId = Integer.valueOf(validatedRequest.get("director_id"));
            int ageClassificationId = Integer.valueOf(validatedRequest.get("age_classification_id"));
            
            AgeClassification ageClassification = ageClassificationJpaController.findAgeClassification(ageClassificationId);
            if (ageClassification == null) {
                throw new ValidateException("La clasificación de edad seleccionada no existe.");
            }
            Director director = directorJpaController.findDirector(directorId);
            if (director == null) {
                throw new ValidateException("El director seleccionado no existe.");
            }
            Distributor distributor = distributorJpaController.findDistributor(distributorId);
            if (distributor == null) {
                throw new ValidateException("El distribuidor seleccionado no existe.");
            }
            Genre genre = genreJpaController.findGenre(genreId);
            if (genre == null) {
                throw new ValidateException("El género seleccionado no existe.");
            }
            Nationality nationality = nationalityJpaController.findNationality(nationalityId);
            if (nationality == null) {
                throw new ValidateException("La nacionalidad seleccionada no existe.");
            }
            
            Movie m = new Movie();
            m.setName(validatedRequest.get("name"));
            m.setWeb(validatedRequest.get("web"));
            m.setOriginalTitle(validatedRequest.get("original_title"));
            m.setDuration(duration);
            m.setYear(year);
            m.setAgeClassificationId(ageClassification);
            m.setDirectorId(director);
            m.setDistributorId(distributor);
            m.setGenreId(genre);
            m.setNationalityId(nationality);
            
            return m;
    }
}