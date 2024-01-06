package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import web.practicafinal.controllers.validations.DistributorCreateDTO;
import web.practicafinal.controllers.validations.DistributorUpdateDTO;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Distributor;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.InstanceConverter;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
@WebServlet("/distributor/*")
public class DistributorController extends HttpServlet {

    public DistributorController() {
        super();
    }

    /*
    /distributor -> Ver lista con todos los distribuidores
    /distributor/{id} -> Ver información del distribuidor con id = {id}
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String distributorIdStr = Request.getURLValue(request);
        
        if (distributorIdStr == null) {
            List<Distributor> distributors = ModelController.getDistributor().findDistributorEntities();
            Response.outputData(response, 200, distributors);
            return;
        }
        
        int distributorId = Integer.parseInt(distributorIdStr);
        Distributor distributor = ModelController.getDistributor().findDistributor(distributorId);
        if (distributor == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el distribuidor solicitado");
            return;
        }
        Response.outputData(response, 200, distributor);
        
    }
    
    /*
    /distributor -> Crear distribuidor
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        try {
            DistributorCreateDTO distributorCreateDTO = new DistributorCreateDTO(request.getParameter("name"));

            Request.validateViolations(distributorCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        Distributor distributor = new Distributor();
        distributor.setName(request.getParameter("name"));

        try {
            ModelController.getDistributor().create(distributor);
            Response.outputData(response, 200, distributor);
        } catch (Exception ex) {
            CustomLogger.errorThrow(DistributorController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    
    /*
    /distributor/{id} -> Actualizar distribuidor con id = {id}
    */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        DistributorUpdateDTO distributorUpdateDTO = null;
        try {
            distributorUpdateDTO = new DistributorUpdateDTO(request.getParameter("name"));

            Request.validateViolations(distributorUpdateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        String distributorIdStr = Request.getURLValue(request);
        
        if (distributorIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ningún distribuidor.");
            return;
        }
        
        int distributorId = Integer.parseInt(distributorIdStr);
        
        Distributor distributor = ModelController.getDistributor().findDistributor(distributorId);
        if (distributor == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el distribuidor solicitado");
            return;
        }

        InstanceConverter.updateInstance(Distributor.class, distributor, DistributorUpdateDTO.class, distributorUpdateDTO);
        
        try {
            ModelController.getDistributor().edit(distributor);
            Response.outputData(response, 200, distributor);
        } catch (Exception ex) {
            CustomLogger.errorThrow(DistributorController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }
    
    /*
    /distributor/{id} -> Eliminar distribuidor con id = {id}
    */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String distributorIdStr = Request.getURLValue(request);
        
        if (distributorIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ningún distribuidor.");
            return;
        }
        
        int distributorId = Integer.parseInt(distributorIdStr);
        
        Distributor distributor = ModelController.getDistributor().findDistributor(distributorId);
        if (distributor == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el distribuidor solicitado");
            return;
        }
        
        try {
            ModelController.getDistributor().destroy(distributorId);
            Response.outputMessage(response, 200, "Género eliminado correctamente");
        } catch (RollbackFailureException ex) {
            CustomLogger.errorThrow(DistributorController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el distribuidor");
        } catch (Exception ex) {
            CustomLogger.errorThrow(DistributorController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el distribuidor");
        }
        
    }
    
}