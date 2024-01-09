package web.practicafinal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import web.practicafinal.controllers.validations.MovieCreateDTO;
import web.practicafinal.controllers.validations.MovieUpdateDTO;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.UnauthorizedException;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Actor;
import web.practicafinal.models.AgeClassification;
import web.practicafinal.models.Comment;
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
import web.practicafinal.models.helpers.ActorHelper;
import web.practicafinal.models.helpers.MovieHelper;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.InstanceConverter;
import web.practicafinal.utils.Middleware;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/movie/*")
public class MovieController extends HttpServlet {

    public MovieController() {
        super();
    }

    /*
    /movie -> Ver lista paginada con todas las peliculas
    /movie/{id} -> Ver información de la película con id = {id}
    /movie/{id}/comment -> Ver comentarios paginados de película con id = {id}
    /movie/available -> Ver lista paginada con todas las peliculas disponibles para ver
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        
        //////////////////////
        // RUTA PÚBLICA
        //////////////////////
        
        // Si está llamado a /movie/{id}/comment
        if (request.getRequestURI().endsWith("/comment")) {
            doGetComment(request, response);
            return;
        }
        
        String movieIdStr = Request.getURLValue(request);
        
        if (movieIdStr == null) {
            Map<String, Integer> integers = null;
            try {
                integers = Request.validateInteger(request, "page");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;            
            }
            int actualPage = integers.get("page") != null ? integers.get("page") : 1;
            Response.outputData(response, 200, PaginationHelper.getPaginated(Movie.class, actualPage, request.getParameter("name")), 5);
            return;
        }
        
        if (movieIdStr.equalsIgnoreCase("available")) {
            doGetMovieAvailable(request, response);
            return;
        }
        
        if (movieIdStr.equalsIgnoreCase("all")) {
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
        
        //////////////////////
        // RUTA SOLO PARA ADMINS
        //////////////////////
        try {
            Middleware.adminRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        } catch (UnauthorizedException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;        
        }
        
        // Si está llamado a /movie/{id}/actors
        if (request.getRequestURI().endsWith("/actor")) {
            doPostActor(request, response);
            return;
        }
        
        // Validar parámetros de la solicitud
        Map<String, Short> shorts = null;
        Map<String, Integer> integers = null;
        try {
            shorts = Request.validateShort(request, "duration", "year");
            integers = Request.validateInteger(request, "genre_id", "nationality_id", "distributor_id", "director_id", "age_classification_id");
            
            MovieCreateDTO movieCreateDTO = new MovieCreateDTO(request.getParameter("name"), request.getParameter("web"), request.getParameter("original_title"),
                shorts.get("year"), shorts.get("duration"), request.getParameter("synopsis"), integers.get("genre_id"), integers.get("nationality_id"), integers.get("distributor_id"), 
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
        movie.setSynopsis(request.getParameter("synopsis"));
        movie.setAgeClassification(ageClassification);
        movie.setDirector(director);
        movie.setDistributor(distributor);
        movie.setGenre(genre);
        movie.setNationality(nationality);

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
        
        //////////////////////
        // RUTA SOLO PARA ADMINS
        //////////////////////
        try {
            Middleware.adminRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        } catch (UnauthorizedException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;        
        }
        
        // Validar parámetros de la solicitud
        MovieUpdateDTO movieUpdateDTO = null;
        try {
            Map<String, Short> shorts = Request.validateShort(request, "duration", "year");
            Map<String, Integer> integers = Request.validateInteger(request, "genre_id", "nationality_id", "distributor_id", "director_id", "age_classification_id");

            movieUpdateDTO = new MovieUpdateDTO(request.getParameter("name"), request.getParameter("web"), request.getParameter("original_title"),
                shorts.get("year"), shorts.get("duration"), request.getParameter("synopsis"), integers.get("genre_id"), integers.get("nationality_id"), integers.get("distributor_id"), 
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
        
        //////////////////////
        // RUTA SOLO PARA ADMINS
        //////////////////////
        try {
            Middleware.adminRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        } catch (UnauthorizedException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;        
        }

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
    
    private void doPostActor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String movieIdStr = Request.getURLValue(request);
        if (movieIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna película.");
            return;
        }
        int movieId = Integer.parseInt(movieIdStr);
        
        List<Integer> numbers = new ArrayList<Integer>();
        
        // Leer las ids de los actores y guardarlas en una lista de enteros
        boolean continueReading = true;
        int count = 0;
        while (continueReading) {
            String parameterStr = request.getParameter(""+count);
            if (parameterStr != null) {
                numbers.add(Integer.parseInt(parameterStr));
                count++;
            } else {
                continueReading = false;
            }
        }
        
        List<Actor> actors = new ArrayList<Actor>();

        if (!numbers.isEmpty()) {
            actors = ActorHelper.getByIds(numbers);
        }
        Movie movie = ModelController.getMovie().findMovie(movieId);
        if (movie == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la película solicitada");
            return;
        }
        movie.setActorList(actors);
        
        try {
            ModelController.getMovie().edit(movie);
            Response.outputData(response, 200, movie);
        } catch (Exception ex) {
            CustomLogger.errorThrow(MovieController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
        Response.outputMessage(response, 200, "Actores actualizados correctamente");
        return;
    }
    
    
    private void doGetComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
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
        
        // Validar parámetros de la solicitud
        Map<String, Integer> integers = null;
        try {
            integers = Request.validateInteger(request, "page");
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;            
        }
        int actualPage = integers.get("page") != null ? integers.get("page") : 1;
        Map<String, Object> mapParameters = new HashMap<>();
        mapParameters.put("movie", movie);
        Response.outputData(response, 200, PaginationHelper.getPaginatedWithFilters(Comment.class, actualPage, mapParameters), 4);
        return;
        
    }
    
    private void doGetMovieAvailable(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Map<String, Integer> integers = null;
        try {
            integers = Request.validateInteger(request, "page");
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;            
        }
        String movieName = request.getParameter("name");
        if (movieName == null) movieName = "";
        int actualPage = integers.get("page") != null ? integers.get("page") : 1;
        Response.outputData(response, 200, PaginationHelper.getPaginatedWithQuery(Movie.class, actualPage, MovieHelper.getAvailablesMoviesQuery(movieName), MovieHelper.getAvailablesMoviesTotalCountQuery(movieName)), 4);
        return;
    
    }
    
}