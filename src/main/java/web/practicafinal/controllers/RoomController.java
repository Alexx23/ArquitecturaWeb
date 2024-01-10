package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import web.practicafinal.controllers.validations.RoomCreateDTO;
import web.practicafinal.controllers.validations.RoomUpdateDTO;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.UnauthorizedException;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Room;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.models.helpers.RoomHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.InstanceConverter;
import web.practicafinal.utils.Middleware;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/room/*")
public class RoomController extends HttpServlet {

    public RoomController() {
        super();
    }

    /*
    /room -> Ver lista paginada con todas las salas
    /room/{id} -> Ver información de la sala con id = {id}
    /room/all -> Ver lista con todos
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
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
        
        String roomIdStr = Request.getURLValue(request);
        
        if (roomIdStr == null) {
            Map<String, Integer> integers = null;
            try {
                integers = Request.validateInteger(request, "page");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;            
            }
            int actualPage = integers.get("page") != null ? integers.get("page") : 1;
            Response.outputData(response, 200, PaginationHelper.getPaginated(Room.class, actualPage, request.getParameter("name")), 4);
            return;
        }
        
        if (roomIdStr.equalsIgnoreCase("all")) {
            List<Room> rooms = ModelController.getRoom().findRoomEntities();
            Response.outputData(response, 200, rooms);
            return;
        }
        
        int roomId = Integer.parseInt(roomIdStr);
        Room room = ModelController.getRoom().findRoom(roomId);
        if (room == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la sala solicitada");
            return;
        }
        Response.outputData(response, 200, room);
        
    }
    
    /*
    /room -> Crear sala
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
        Map<String, Short> shorts = null;
        try {
            shorts = Request.validateShort(request, "depth", "seats");
            
            RoomCreateDTO roomCreateDTO = new RoomCreateDTO(request.getParameter("name"), shorts.get("depth"), shorts.get("seats"));

            Request.validateViolations(roomCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        Room room = new Room();
        room.setName(request.getParameter("name"));
        room.setDepth(shorts.get("depth"));
        room.setSeats(shorts.get("seats"));

        try {
            ModelController.getRoom().create(room);
            Response.outputData(response, 200, room);
        } catch (Exception ex) {
            CustomLogger.errorThrow(RoomController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    
    /*
    /room/{id} -> Actualizar sala con id = {id}
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
        RoomUpdateDTO roomUpdateDTO = null;
        try {
            Map<String, Short> shorts = Request.validateShort(request, "depth", "seats");

            roomUpdateDTO = new RoomUpdateDTO(request.getParameter("name"), shorts.get("depth"), shorts.get("seats"));

            Request.validateViolations(roomUpdateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        String roomIdStr = Request.getURLValue(request);
        
        if (roomIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna sala.");
            return;
        }
        
        int roomId = Integer.parseInt(roomIdStr);
        
        Room room = ModelController.getRoom().findRoom(roomId);
        if (room == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la sala solicitada");
            return;
        }
        
        // Comprobar que el nombre de la sala sea único
        if (request.getParameter("name") != null && 
                !room.getName().equalsIgnoreCase(request.getParameter("name")) &&
                RoomHelper.getRoomByName(request.getParameter("name")) != null) {
            Response.outputMessage(response, 400, "Ese nombre ya está siendo utilizado por otra sala");
            return;
        }

        InstanceConverter.updateInstance(Room.class, room, RoomUpdateDTO.class, roomUpdateDTO);
        
        try {
            ModelController.getRoom().edit(room);
            Response.outputData(response, 200, room);
        } catch (Exception ex) {
            CustomLogger.errorThrow(RoomController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }
    
    /*
    /room/{id} -> Eliminar sala con id = {id}
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

        String roomIdStr = Request.getURLValue(request);
        
        if (roomIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna sala.");
            return;
        }
        
        int roomId = Integer.parseInt(roomIdStr);
        
        Room room = ModelController.getRoom().findRoom(roomId);
        if (room == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la sala solicitada");
            return;
        }
        
        try {
            ModelController.getRoom().destroy(roomId);
            Response.outputMessage(response, 200, "Sala eliminada correctamente");
        } catch (RollbackFailureException ex) {
            CustomLogger.errorThrow(RoomController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar la sala");
        } catch (Exception ex) {
            CustomLogger.errorThrow(RoomController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar la sala");
        }
        
    }
    
}