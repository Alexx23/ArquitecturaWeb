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
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Room;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.RoomJpaController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.InstanceConverter;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
@WebServlet("/room/*")
public class RoomController extends HttpServlet {
    
    private static RoomJpaController roomJpaController = null;

    public RoomController() {
        super();
    }
    
    @Override
    public void init() {
        roomJpaController = ModelController.getRoom();
    }

    /*
    /room -> Ver lista con todas las salas
    /room/{id} -> Ver información de la sala con id = {id}
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String roomIdStr = Request.getURLValue(request);
        
        if (roomIdStr == null) {
            List<Room> rooms = roomJpaController.findRoomEntities();
            Response.outputData(response, 200, rooms);
            return;
        }
        
        int roomId = Integer.parseInt(roomIdStr);
        Room room = roomJpaController.findRoom(roomId);
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
        
        // Validar parámetros de la solicitud
        try {
            Map<String, Short> shorts = Request.validateShort(request, "files", "cols");
            
            RoomCreateDTO roomCreateDTO = new RoomCreateDTO(request.getParameter("name"), shorts.get("files"), shorts.get("cols"));

            Request.validateViolations(roomCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        Room room = new Room();
        room.setName(request.getParameter("name"));
        room.setFiles((short)Integer.parseInt(request.getParameter("files")));
        room.setCols((short)Integer.parseInt(request.getParameter("cols")));

        try {
            roomJpaController.create(room);
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
        
        // Validar parámetros de la solicitud
        RoomUpdateDTO roomUpdateDTO = null;
        try {
            Map<String, Short> shorts = Request.validateShort(request, "files", "cols");

            roomUpdateDTO = new RoomUpdateDTO(request.getParameter("name"), shorts.get("files"), shorts.get("cols"));

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
        
        Room room = roomJpaController.findRoom(roomId);
        if (room == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la sala solicitada");
            return;
        }

        InstanceConverter.updateInstance(Room.class, room, RoomUpdateDTO.class, roomUpdateDTO);
        
        try {
            roomJpaController.edit(room);
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

        String roomIdStr = Request.getURLValue(request);
        
        if (roomIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna sala.");
            return;
        }
        
        int roomId = Integer.parseInt(roomIdStr);
        
        Room room = roomJpaController.findRoom(roomId);
        if (room == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la sala solicitada");
            return;
        }
        
        try {
            roomJpaController.destroy(roomId);
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