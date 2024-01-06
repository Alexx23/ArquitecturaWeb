package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import web.practicafinal.controllers.validations.DirectorCreateDTO;
import web.practicafinal.controllers.validations.DirectorUpdateDTO;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Director;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.InstanceConverter;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
@WebServlet("/director/*")
public class DirectorController extends HttpServlet {

    public DirectorController() {
        super();
    }

    /*
    /director -> Ver lista con todos los directores
    /director/{id} -> Ver información del director con id = {id}
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String directorIdStr = Request.getURLValue(request);
        
        if (directorIdStr == null) {
            Map<String, Integer> integers = null;
            try {
                integers = Request.validateInteger(request, "page");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;            
            }
            int actualPage = integers.get("page") != null ? integers.get("page") : 1;
            Response.outputData(response, 200, PaginationHelper.getPaginated(Director.class, actualPage, request.getParameter("name")));
            return;
        }
        
        int directorId = Integer.parseInt(directorIdStr);
        Director director = ModelController.getDirector().findDirector(directorId);
        if (director == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el director solicitado");
            return;
        }
        Response.outputData(response, 200, director);
        
    }
    
    /*
    /director -> Crear director
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        try {
            DirectorCreateDTO directorCreateDTO = new DirectorCreateDTO(request.getParameter("name"));

            Request.validateViolations(directorCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        Director director = new Director();
        director.setName(request.getParameter("name"));

        try {
            ModelController.getDirector().create(director);
            Response.outputData(response, 200, director);
        } catch (Exception ex) {
            CustomLogger.errorThrow(DirectorController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    
    /*
    /director/{id} -> Actualizar director con id = {id}
    */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        DirectorUpdateDTO directorUpdateDTO = null;
        try {
            directorUpdateDTO = new DirectorUpdateDTO(request.getParameter("name"));

            Request.validateViolations(directorUpdateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        String directorIdStr = Request.getURLValue(request);
        
        if (directorIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ningún director.");
            return;
        }
        
        int directorId = Integer.parseInt(directorIdStr);
        
        Director director = ModelController.getDirector().findDirector(directorId);
        if (director == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el director solicitado");
            return;
        }

        InstanceConverter.updateInstance(Director.class, director, DirectorUpdateDTO.class, directorUpdateDTO);
        
        try {
            ModelController.getDirector().edit(director);
            Response.outputData(response, 200, director);
        } catch (Exception ex) {
            CustomLogger.errorThrow(DirectorController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }
    
    /*
    /director/{id} -> Eliminar director con id = {id}
    */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String directorIdStr = Request.getURLValue(request);
        
        if (directorIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ningún director.");
            return;
        }
        
        int directorId = Integer.parseInt(directorIdStr);
        
        Director director = ModelController.getDirector().findDirector(directorId);
        if (director == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el director solicitado");
            return;
        }
        
        try {
            ModelController.getDirector().destroy(directorId);
            Response.outputMessage(response, 200, "Género eliminado correctamente");
        } catch (RollbackFailureException ex) {
            CustomLogger.errorThrow(DirectorController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el director");
        } catch (Exception ex) {
            CustomLogger.errorThrow(DirectorController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el director");
        }
        
    }
    
}