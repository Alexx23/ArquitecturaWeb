package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import web.practicafinal.controllers.validations.SessionCreateDTO;
import web.practicafinal.controllers.validations.SessionUpdateDTO;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.UnauthorizedException;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Movie;
import web.practicafinal.models.Room;
import web.practicafinal.models.Session;
import web.practicafinal.models.Ticket;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.SessionJpaController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.utils.CustomLogger;
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
    /session -> Ver lista paginada con todas las sesiones
    /session/{id} -> Ver información de la sesión con id = {id}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //////////////////////
        // RUTA PÚBLICA
        //////////////////////
        String sessionIdStr = Request.getURLValue(request);

        if (sessionIdStr == null) {
            Map<String, Integer> integers = null;
            try {
                integers = Request.validateInteger(request, "page");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;
            }
            int actualPage = integers.get("page") != null ? integers.get("page") : 1;
            Response.outputData(response, 200, PaginationHelper.getPaginated(Session.class, actualPage, null), 4);
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
    /session/{id}/ticket -> Registrar un ticket en la sesion id
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionIdStr = Request.getURLValue(request);
        if (sessionIdStr == null) {
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
        } else {
            int sessionId = Integer.parseInt(sessionIdStr);
            //obtenemos la sesion con id
            Session session = ModelController.getSession().findSession(sessionId);
            if (session == null) {
                Response.outputMessage(response, 404, "No se ha encontrado la sesión solicitada");
                return;
            }
            //comprobamos que el asiento no este ocupado, si lo esta salimos
            List<Ticket> tickets = session.getTicketList();
            Short row = Short.valueOf(request.getParameter("row"));
            Short col = Short.valueOf(request.getParameter("col"));
            String code = request.getParameter("code");

            for (int i = 0; i < tickets.size(); i++) {
                if (tickets.get(i).getRow() == row && tickets.get(i).getCol() == col) {
                    Response.outputMessage(response, 500, "El asiento elegido ya esta ocupado");//no se que tipo de error deberia ser
                    return;
                }
            }
            //creamos el ticket
            Ticket ticket = new Ticket();
            ticket.setRow(row);
            ticket.setCol(col);
            ticket.setCode(code);
            Calendar calendar = Calendar.getInstance();
            Date fechaActual = calendar.getTime();
            ticket.setCreatedAt(fechaActual);
            tickets.add(ticket);
            session.setTicketList(tickets);
            try {
                ModelController.getSession().edit(session);
                Response.outputData(response, 200, session);
                return;
            } catch (Exception ex) {
                CustomLogger.errorThrow(RegisterController.class.getName(), ex);
                Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
                return;
            }
        }
    }

    /*
    /session/{id} -> Actualizar sesión con id = {id}
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
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
