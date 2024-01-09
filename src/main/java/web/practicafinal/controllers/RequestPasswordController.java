
package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import web.practicafinal.controllers.validations.RequestPasswordDTO;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.User;
import web.practicafinal.models.helpers.UserHelper;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
@WebServlet("/request-password/*")
public class RequestPasswordController extends HttpServlet {

    public RequestPasswordController() {
        super();
    }
    
    /*
    /request-password -> Enviar mensaje al email indicado para recuperar la contraseña
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA PÚBLICA
        //////////////////////

        // Validar parámetros de la solicitud
        RequestPasswordDTO requestPasswordDTO = null;
        try {
            requestPasswordDTO = new RequestPasswordDTO(request.getParameter("email"));

            Request.validateViolations(requestPasswordDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        
        User user = UserHelper.getUserByEmail(request.getParameter("email"));
        if (user == null) {
            Response.outputMessage(response, 400, "No existe ningún usuario con ese email.");
            return;
        }
        
        // Aquí habría que enviar un email usando SMTP, preferiblemente asíncrono
        
        Response.outputMessage(response, 200, "Email enviado correctamente.");
    }
    
}
