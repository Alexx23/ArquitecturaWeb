package web.practicafinal.controllers;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import web.practicafinal.controllers.validations.SessionCreateDTO;
import web.practicafinal.controllers.validations.SessionUpdateDTO;
import web.practicafinal.controllers.validations.TicketCreateDTO;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.UnauthorizedException;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Movie;
import web.practicafinal.models.Room;
import web.practicafinal.models.Session;
import web.practicafinal.models.Ticket;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.SessionJpaController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.models.helpers.SessionHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.CypherUtils;
import web.practicafinal.utils.InstanceConverter;
import web.practicafinal.utils.Middleware;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/session/*")
public class SessionController extends HttpServlet {

    public SessionController() {
        super();
    }

    /*
    /session -> Ver lista con todas las sesiones por día o película
    /session/{id} -> Ver información de la sesión con id = {id}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //////////////////////
        // RUTA PÚBLICA
        //////////////////////
        String sessionIdStr = Request.getURLValue(request);

        if (sessionIdStr == null) {
            // Validar parámetros de la solicitud
            Map<String, Integer> integers = null;
            Map<String, Date> dates = null;
            try {
                integers = Request.validateInteger(request, "movie_id");
                dates = Request.validateDate(request, "date");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;
            }
            
            if (dates.get("date") != null) {                
                List<Session> sessionList = SessionHelper.getByDay(dates.get("date"));
                Response.outputData(response, 200, sessionList);
                return;
            }
            
            if (integers.get("movie_id") != null) {
                Movie movie = ModelController.getMovie().findMovie(integers.get("movie_id"));
                if (movie == null) {
                    Response.outputMessage(response, 400, "No se ha encontrado la película solicitada.");
                    return;
                }
                List<Session> sessionList = SessionHelper.getAvailablesByMovie(movie);
                Response.outputData(response, 200, sessionList, 4);
                return;
            }
            
            Response.outputData(response, 400, "El campo date o movie_id son obligatorio");
            return;
        }

        int sessionId = Integer.parseInt(sessionIdStr);
        Session session = ModelController.getSession().findSession(sessionId);
        if (session == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la sesión solicitada");
            return;
        }
        Response.outputData(response, 200, session);

    }

    /*
    /session -> Crear sesión
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


        // Validar parámetros de la solicitud
        Map<String, Integer> integers = null;
        Map<String, Date> dates = null;
        try {
            integers = Request.validateInteger(request, "movie_id", "room_id");
            dates = Request.validateDate(request, "datetime");

            SessionCreateDTO sessionCreateDTO = new SessionCreateDTO(integers.get("movie_id"), integers.get("room_id"), dates.get("datetime"));

            Request.validateViolations(sessionCreateDTO);

        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        int movieId = integers.get("movie_id");
        int roomId = integers.get("room_id");

        Movie movie = ModelController.getMovie().findMovie(movieId);
        Room room = ModelController.getRoom().findRoom(roomId);

        Session session = new Session();
        session.setMovie(movie);
        session.setRoom(room);
        session.setDatetime(dates.get("datetime"));

        try {
            ModelController.getSession().create(session);
            Response.outputData(response, 200, session);
        } catch (Exception ex) {
            CustomLogger.errorThrow(SessionController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    
    }

    /*
    /session/{id} -> Actualizar sesión con id = {id}
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
        SessionUpdateDTO sessionUpdateDTO = null;
        try {
            Map<String, Integer> integers = Request.validateInteger(request, "movie_id", "room_id");
            Map<String, Date> dates = Request.validateDate(request, "datetime");

            sessionUpdateDTO = new SessionUpdateDTO(integers.get("movie_id"), integers.get("room_id"), dates.get("datetime"));

            Request.validateViolations(sessionUpdateDTO);

        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        String sessionIdStr = Request.getURLValue(request);

        if (sessionIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna sesión.");
            return;
        }

        int sessionId = Integer.parseInt(sessionIdStr);

        Session session = ModelController.getSession().findSession(sessionId);
        if (session == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la sesión solicitada");
            return;
        }

        InstanceConverter.updateInstance(Session.class, session, SessionUpdateDTO.class, sessionUpdateDTO);

        try {
            ModelController.getSession().edit(session);
            Response.outputData(response, 200, session);
        } catch (Exception ex) {
            CustomLogger.errorThrow(SessionController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }

    /*
    /session/{id} -> Eliminar sesión con id = {id}
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

        String sessionIdStr = Request.getURLValue(request);

        if (sessionIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna sesión.");
            return;
        }

        int sessionId = Integer.parseInt(sessionIdStr);

        Session session = ModelController.getSession().findSession(sessionId);
        if (session == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la sesión solicitada");
            return;
        }

        try {
            ModelController.getSession().destroy(sessionId);
            Response.outputMessage(response, 200, "Sesión eliminada correctamente");
        } catch (RollbackFailureException ex) {
            CustomLogger.errorThrow(SessionController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar la sesión");
        } catch (Exception ex) {
            CustomLogger.errorThrow(SessionController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar la sesión");
        }

    }

}
