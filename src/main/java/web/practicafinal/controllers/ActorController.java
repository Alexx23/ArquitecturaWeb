package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import web.practicafinal.controllers.validations.ActorCreateDTO;
import web.practicafinal.controllers.validations.ActorUpdateDTO;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.UnauthorizedException;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Actor;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.InstanceConverter;
import web.practicafinal.utils.Middleware;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/actor/*")
public class ActorController extends HttpServlet {

    public ActorController() {
        super();
    }

    /*
    /actor -> Ver lista paginada con todos los actores
    /actor/{id} -> Ver información del actor con id = {id}
    /actor/all -> Ver lista con todos
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
        
        String actorIdStr = Request.getURLValue(request);
        
        if (actorIdStr == null) {
            Map<String, Integer> integers = null;
            try {
                integers = Request.validateInteger(request, "page");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;            
            }
            int actualPage = integers.get("page") != null ? integers.get("page") : 1;
            Response.outputData(response, 200, PaginationHelper.getPaginated(Actor.class, actualPage, request.getParameter("name")), 4);
            return;
        }
        
        if (actorIdStr.equalsIgnoreCase("all")) {
            List<Actor> actors = ModelController.getActor().findActorEntities();
            Response.outputData(response, 200, actors);
            return;
        }
        
        int actorId = Integer.parseInt(actorIdStr);
        Actor actor = ModelController.getActor().findActor(actorId);
        if (actor == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el actor solicitado");
            return;
        }
        Response.outputData(response, 200, actor);
        
    }
    
    /*
    /actor -> Crear actor
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
        try {
            ActorCreateDTO actorCreateDTO = new ActorCreateDTO(request.getParameter("name"));

            Request.validateViolations(actorCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        Actor actor = new Actor();
        actor.setName(request.getParameter("name"));

        try {
            ModelController.getActor().create(actor);
            Response.outputData(response, 200, actor);
        } catch (Exception ex) {
            CustomLogger.errorThrow(ActorController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    
    /*
    /actor/{id} -> Actualizar actor con id = {id}
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
        ActorUpdateDTO actorUpdateDTO = null;
        try {
            actorUpdateDTO = new ActorUpdateDTO(request.getParameter("name"));

            Request.validateViolations(actorUpdateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        String actorIdStr = Request.getURLValue(request);
        
        if (actorIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ningún actor.");
            return;
        }
        
        int actorId = Integer.parseInt(actorIdStr);
        
        Actor actor = ModelController.getActor().findActor(actorId);
        if (actor == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el actor solicitado");
            return;
        }

        InstanceConverter.updateInstance(Actor.class, actor, ActorUpdateDTO.class, actorUpdateDTO);
        
        try {
            ModelController.getActor().edit(actor);
            Response.outputData(response, 200, actor);
        } catch (Exception ex) {
            CustomLogger.errorThrow(ActorController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }
    
    /*
    /actor/{id} -> Eliminar actor con id = {id}
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

        String actorIdStr = Request.getURLValue(request);
        
        if (actorIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ningún actor.");
            return;
        }
        
        int actorId = Integer.parseInt(actorIdStr);
        
        Actor actor = ModelController.getActor().findActor(actorId);
        if (actor == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el actor solicitado");
            return;
        }
        
        try {
            ModelController.getActor().destroy(actorId);
            Response.outputMessage(response, 200, "Género eliminado correctamente");
        } catch (RollbackFailureException ex) {
            CustomLogger.errorThrow(ActorController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el actor");
        } catch (Exception ex) {
            CustomLogger.errorThrow(ActorController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el actor");
        }
        
    }
    
}