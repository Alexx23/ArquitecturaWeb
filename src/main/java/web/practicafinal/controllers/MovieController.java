package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import web.practicafinal.controllers.validations.MovieCreateDTO;
import web.practicafinal.controllers.validations.MovieUpdateDTO;
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
import web.practicafinal.utils.InstanceConverter;
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
        
        // Validar parámetros de la solicitud
        try {
            Map<String, Short> shorts = Request.validateShort(request, "duration", "year");
            Map<String, Integer> integers = Request.validateInteger(request, "genre_id", "nationality_id", "distributor_id", "director_id", "age_classification_id");
            
            MovieCreateDTO movieCreateDTO = new MovieCreateDTO(request.getParameter("name"), request.getParameter("web"), request.getParameter("original_title"),
                shorts.get("year"), shorts.get("duration"), integers.get("genre_id"), integers.get("nationality_id"), integers.get("distributor_id"), 
                integers.get("director_id"), integers.get("age_classification_id"));

            Request.validateViolations(movieCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        int genreId = Integer.parseInt(request.getParameter("genre_id"));
        int nationalityId = Integer.parseInt(request.getParameter("nationality_id"));
        int distributorId = Integer.parseInt(request.getParameter("distributor_id"));
        int directorId = Integer.parseInt(request.getParameter("director_id"));
        int ageClassificationId = Integer.parseInt(request.getParameter("age_classification_id"));
        
        AgeClassification ageClassification = ageClassificationJpaController.findAgeClassification(ageClassificationId);
        Director director = directorJpaController.findDirector(directorId);
        Distributor distributor = distributorJpaController.findDistributor(distributorId);
        Genre genre = genreJpaController.findGenre(genreId);
        Nationality nationality = nationalityJpaController.findNationality(nationalityId);
        
        Movie movie = new Movie();
        movie.setName(request.getParameter("name"));
        movie.setWeb(request.getParameter("web"));
        movie.setOriginalTitle(request.getParameter("original_title"));
        movie.setDuration((short)Integer.parseInt(request.getParameter("duration")));
        movie.setYear((short)Integer.parseInt(request.getParameter("year")));
        movie.setAgeClassificationId(ageClassification);
        movie.setDirectorId(director);
        movie.setDistributorId(distributor);
        movie.setGenreId(genre);
        movie.setNationalityId(nationality);

        try {
            movieJpaController.create(movie);
            Response.outputData(response, 200, movie, true);
        } catch (Exception ex) {
            CustomLogger.errorThrow(MovieController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    
    /*
    /movie/{id} -> Actualizar pelicula con id = {id}
    */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        MovieUpdateDTO movieUpdateDTO = null;
        try {
            Map<String, Short> shorts = Request.validateShort(request, "duration", "year");
            Map<String, Integer> integers = Request.validateInteger(request, "genre_id", "nationality_id", "distributor_id", "director_id", "age_classification_id");

            movieUpdateDTO = new MovieUpdateDTO(request.getParameter("name"), request.getParameter("web"), request.getParameter("original_title"),
                shorts.get("year"), shorts.get("duration"), integers.get("genre_id"), integers.get("nationality_id"), integers.get("distributor_id"), 
                integers.get("director_id"), integers.get("age_classification_id"));

            Request.validateViolations(movieUpdateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        String movieIdStr = Request.getURLValue(request);
        
        if (movieIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna película.");
            return;
        }
        
        int movieId = Integer.parseInt(movieIdStr);
        
        Movie movie = movieJpaController.findMovie(movieId);
        if (movie == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la película solicitada");
            return;
        }

        InstanceConverter.updateInstance(Movie.class, movie, MovieUpdateDTO.class, movieUpdateDTO);
        
        try {
            movieJpaController.edit(movie);
            Response.outputData(response, 200, movie, true);
        } catch (Exception ex) {
            CustomLogger.errorThrow(MovieController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }
    
    /*
    /movie/{id} -> Eliminar película con id = {id}
    */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String movieIdStr = Request.getURLValue(request);
        
        if (movieIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna película.");
            return;
        }
        
        int movieId = Integer.parseInt(movieIdStr);
        
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
    
}