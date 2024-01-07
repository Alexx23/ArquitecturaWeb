package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import web.practicafinal.controllers.validations.NationalityCreateDTO;
import web.practicafinal.controllers.validations.NationalityUpdateDTO;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Nationality;
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
@WebServlet("/nationality/*")
public class NationalityController extends HttpServlet {

    public NationalityController() {
        super();
    }

    /*
    /nationality -> Ver lista con todas las nacionalidades
    /nationality/{id} -> Ver información de la nacionalidad con id = {id}
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String nationalityIdStr = Request.getURLValue(request);
        
        if (nationalityIdStr == null) {
            Map<String, Integer> integers = null;
            try {
                integers = Request.validateInteger(request, "page");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;            
            }
            int actualPage = integers.get("page") != null ? integers.get("page") : 1;
            Response.outputData(response, 200, PaginationHelper.getPaginated(Nationality.class, actualPage, request.getParameter("name")), 4);
            return;
        }
        
        if (nationalityIdStr.equalsIgnoreCase("all")) {
            List<Nationality> nationalities = ModelController.getNationality().findNationalityEntities();
            Response.outputData(response, 200, nationalities);
            return;
        }
        
        int nationalityId = Integer.parseInt(nationalityIdStr);
        Nationality nationality = ModelController.getNationality().findNationality(nationalityId);
        if (nationality == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la nacionalidad solicitada");
            return;
        }
        Response.outputData(response, 200, nationality);
        
    }
    
    /*
    /nationality -> Crear nacionalidad
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        try {
            NationalityCreateDTO nationalityCreateDTO = new NationalityCreateDTO(request.getParameter("name"));

            Request.validateViolations(nationalityCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        Nationality nationality = new Nationality();
        nationality.setName(request.getParameter("name"));

        try {
            ModelController.getNationality().create(nationality);
            Response.outputData(response, 200, nationality);
        } catch (Exception ex) {
            CustomLogger.errorThrow(NationalityController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    
    /*
    /nationality/{id} -> Actualizar nacionalidad con id = {id}
    */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        NationalityUpdateDTO nationalityUpdateDTO = null;
        try {
            nationalityUpdateDTO = new NationalityUpdateDTO(request.getParameter("name"));

            Request.validateViolations(nationalityUpdateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        String nationalityIdStr = Request.getURLValue(request);
        
        if (nationalityIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna nacionalidad.");
            return;
        }
        
        int nationalityId = Integer.parseInt(nationalityIdStr);
        
        Nationality nationality = ModelController.getNationality().findNationality(nationalityId);
        if (nationality == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la nacionalidad solicitada");
            return;
        }

        InstanceConverter.updateInstance(Nationality.class, nationality, NationalityUpdateDTO.class, nationalityUpdateDTO);
        
        try {
            ModelController.getNationality().edit(nationality);
            Response.outputData(response, 200, nationality);
        } catch (Exception ex) {
            CustomLogger.errorThrow(NationalityController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }
    
    /*
    /nationality/{id} -> Eliminar nacionalidad con id = {id}
    */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String nationalityIdStr = Request.getURLValue(request);
        
        if (nationalityIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ninguna nacionalidad.");
            return;
        }
        
        int nationalityId = Integer.parseInt(nationalityIdStr);
        
        Nationality nationality = ModelController.getNationality().findNationality(nationalityId);
        if (nationality == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la nacionalidad solicitada");
            return;
        }
        
        try {
            ModelController.getNationality().destroy(nationalityId);
            Response.outputMessage(response, 200, "Género eliminado correctamente");
        } catch (RollbackFailureException ex) {
            CustomLogger.errorThrow(NationalityController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar la nacionalidad");
        } catch (Exception ex) {
            CustomLogger.errorThrow(NationalityController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar la nacionalidad");
        }
        
    }
    
}