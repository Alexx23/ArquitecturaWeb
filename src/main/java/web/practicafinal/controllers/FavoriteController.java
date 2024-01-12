package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import web.practicafinal.controllers.validations.FavoriteDTO;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Favorite;
import web.practicafinal.models.Movie;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.helpers.FavoriteHelper;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.Middleware;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */

@WebServlet("/favorite/*")
public class FavoriteController extends HttpServlet {

    public FavoriteController() {
        super();
    }

    /*
    /favorite -> Obtener lista de películas favoritas
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA PARA CLIENTES
        //////////////////////
        try {
            Middleware.authRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        Map<String, Integer> integers = null;
        try {
            integers = Request.validateInteger(request, "page");
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;            
        }
        
        User userSession;
        try {
            userSession = Request.getUser(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        int actualPage = integers.get("page") != null ? integers.get("page") : 1;
        FavoriteHelper.getFavAvailablesMoviesQuery(userSession);
        FavoriteHelper.getFavAvailablesMoviesTotalCountQuery(userSession);
        Response.outputData(response, 200, PaginationHelper.getPaginatedWithQuery(Favorite.class, actualPage, FavoriteHelper.getFavAvailablesMoviesQuery(userSession), FavoriteHelper.getFavAvailablesMoviesTotalCountQuery(userSession)), 4);

        
    }
    
    /*
    /favorite -> Añadir película a favoritos
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA PARA CLIENTES
        //////////////////////
        try {
            Middleware.authRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Validar parámetros de la solicitud
        Map<String, Integer> integers = null;
        try {
            integers = Request.validateInteger(request, "movie_id");
            
            FavoriteDTO favoriteDTO = new FavoriteDTO(integers.get("movie_id"));

            Request.validateViolations(favoriteDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        int movieId = integers.get("movie_id");
        
        Movie movie = ModelController.getMovie().findMovie(movieId);
        User userSession;
        try {
            userSession = Request.getUser(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        Favorite existingFavorite = FavoriteHelper.getFavorite(userSession, movie);
        if (existingFavorite != null) {
            Response.outputMessage(response, 400, "Ya habías añadido esta película a tu lista de favoritos.");
            return;
        }
        
        Favorite favorite = new Favorite();
        favorite.setMovie(movie);
        favorite.setUser(userSession);
        favorite.setCreatedAt(new Date());

        try {
            ModelController.getFavorite().create(favorite);
            Response.outputData(response, 200, favorite);
        } catch (Exception ex) {
            CustomLogger.errorThrow(FavoriteController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
 
    
    /*
    /favorite -> Eliminar película de favoritos
    */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA PARA CLIENTES
        //////////////////////
        try {
            Middleware.authRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Validar parámetros de la solicitud
        Map<String, Integer> integers = null;
        try {
            integers = Request.validateInteger(request, "movie_id");
            
            FavoriteDTO favoriteDTO = new FavoriteDTO(integers.get("movie_id"));

            Request.validateViolations(favoriteDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        int movieId = integers.get("movie_id");
        
        Movie movie = ModelController.getMovie().findMovie(movieId);
        User userSession;
        try {
            userSession = Request.getUser(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        Favorite existingFavorite = FavoriteHelper.getFavorite(userSession, movie);
        if (existingFavorite == null) {
            Response.outputMessage(response, 400, "No habías añadido esta película a tu lista de favoritos.");
            return;
        }

        try {
            ModelController.getFavorite().destroy(existingFavorite.getId());
            Response.outputData(response, 200, "Película eliminada de favoritos correctamente.");
        } catch (Exception ex) {
            CustomLogger.errorThrow(FavoriteController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
}