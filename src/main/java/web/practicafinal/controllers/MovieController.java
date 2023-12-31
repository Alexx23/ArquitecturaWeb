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

    public MovieController() {
        super();
    }

    /*
    /movie -> Ver lista con todas las peliculas
    /movie/{id} -> Ver información de la película con id = {id}
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String movieIdStr = Request.getURLValue(request);
        
        if (movieIdStr == null) {
            List<Movie> movies = ModelController.getMovie().findMovieEntities();
            Response.outputData(response, 200, movies);
            return;
        }
        
        int movieId = Integer.parseInt(movieIdStr);
        Movie movie = ModelController.getMovie().findMovie(movieId);
        if (movie == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la película solicitada");
            return;
        }
        Response.outputData(response, 200, movie);
        
    }
    
    /*
    /movie -> Crear pelicula
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        Map<String, Short> shorts = null;
        Map<String, Integer> integers = null;
        try {
            shorts = Request.validateShort(request, "duration", "year");
            integers = Request.validateInteger(request, "genre_id", "nationality_id", "distributor_id", "director_id", "age_classification_id");
            
            MovieCreateDTO movieCreateDTO = new MovieCreateDTO(request.getParameter("name"), request.getParameter("web"), request.getParameter("original_title"),
                shorts.get("year"), shorts.get("duration"), integers.get("genre_id"), integers.get("nationality_id"), integers.get("distributor_id"), 
                integers.get("director_id"), integers.get("age_classification_id"));

            Request.validateViolations(movieCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        int genreId = integers.get("genre_id");
        int nationalityId = integers.get("nationality_id");
        int distributorId = integers.get("distributor_id");
        int directorId = integers.get("director_id");
        int ageClassificationId = integers.get("age_classification_id");
        
        AgeClassification ageClassification = ModelController.getAgeClassification().findAgeClassification(ageClassificationId);
        Director director = ModelController.getDirector().findDirector(directorId);
        Distributor distributor = ModelController.getDistributor().findDistributor(distributorId);
        Genre genre = ModelController.getGenre().findGenre(genreId);
        Nationality nationality = ModelController.getNationality().findNationality(nationalityId);
        
        Movie movie = new Movie();
        movie.setName(request.getParameter("name"));
        movie.setWeb(request.getParameter("web"));
        movie.setOriginalTitle(request.getParameter("original_title"));
        movie.setDuration(shorts.get("duration"));
        movie.setYear(shorts.get("year"));
        movie.setAgeClassificationId(ageClassification);
        movie.setDirectorId(director);
        movie.setDistributorId(distributor);
        movie.setGenreId(genre);
        movie.setNationalityId(nationality);

        try {
            ModelController.getMovie().create(movie);
            Response.outputData(response, 200, movie);
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
        
        Movie movie = ModelController.getMovie().findMovie(movieId);
        if (movie == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la película solicitada");
            return;
        }

        InstanceConverter.updateInstance(Movie.class, movie, MovieUpdateDTO.class, movieUpdateDTO);
        
        try {
            ModelController.getMovie().edit(movie);
            Response.outputData(response, 200, movie);
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
        
        Movie movie = ModelController.getMovie().findMovie(movieId);
        if (movie == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la película solicitada");
            return;
        }
        
        try {
            ModelController.getMovie().destroy(movieId);
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