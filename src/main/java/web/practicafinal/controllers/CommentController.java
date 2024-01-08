package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import web.practicafinal.controllers.validations.CommentCreateDTO;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Comment;
import web.practicafinal.models.Movie;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */

@WebServlet("/comment/*")
public class CommentController extends HttpServlet {

    public CommentController() {
        super();
    }
    
    /*
    /comment -> Crear comentario
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        Map<String, Integer> integers = null;
        try {
            integers = Request.validateInteger(request, "movie_id");
            
            CommentCreateDTO commentCreateDTO = new CommentCreateDTO(integers.get("movie_id"), request.getParameter("content"));

            Request.validateViolations(commentCreateDTO);
            
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
        
        Comment comment = new Comment();
        comment.setMovie(movie);
        comment.setUser(userSession);
        comment.setContent(request.getParameter("content"));
        comment.setCreatedAt(new Date());

        try {
            ModelController.getComment().create(comment);
            Response.outputData(response, 200, comment);
        } catch (Exception ex) {
            CustomLogger.errorThrow(CommentController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    /*
    /comment/{id} -> Eliminar comentario con id = {id}
    */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String commentIdStr = Request.getURLValue(request);
        
        if (commentIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguún comentario.");
            return;
        }
        
        int commentId = Integer.parseInt(commentIdStr);
        
        Comment comment = ModelController.getComment().findComment(commentId);
        if (comment == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el comentario solicitado");
            return;
        }
        
        // Obtener usuario
        User userSession;
        try {
            userSession = Request.getUser(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Comprobar que el usuario sea el autor
        if (comment.getUser().getId() != userSession.getId()) {
            Response.outputMessage(response, 400, "No eres el autor del comentario solicitado");
            return;
        }
        
        try {
            ModelController.getComment().destroy(commentId);
            Response.outputMessage(response, 200, "Comentario eliminado correctamente");
        } catch (RollbackFailureException ex) {
            CustomLogger.errorThrow(CommentController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el comentario");
        } catch (Exception ex) {
            CustomLogger.errorThrow(CommentController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el comentario");
        }
        
    }
    
}