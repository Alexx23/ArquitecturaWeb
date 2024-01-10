package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import org.springframework.security.crypto.bcrypt.BCrypt;
import web.practicafinal.controllers.validations.RegisterDTO;
import web.practicafinal.enums.RoleEnum;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Role;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.RoleJpaController;
import web.practicafinal.models.controllers.UserJpaController;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/register")
public class RegisterController extends HttpServlet {

    public RegisterController() {
        super();
    }
    
    /*
    /register -> Registrar usuario
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA PÚBLICA
        //////////////////////
        
        try {
            
            RegisterDTO registerDTO = new RegisterDTO(request.getParameter("name"), request.getParameter("username"), request.getParameter("email"), 
                    request.getParameter("password"), request.getParameter("password_confirmation"));
            
            Request.validateViolations(registerDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        String name = request.getParameter("name");
        String username = request.getParameter("username").toLowerCase();
        String email = request.getParameter("email").toLowerCase();
        String password = request.getParameter("password");
        String passwordConfirmation = request.getParameter("password_confirmation");
        
        if (!password.equals(passwordConfirmation)) {
            Response.outputMessage(response, 400, "Las contraseñas no coinciden");
            return;
        }

        String salt = BCrypt.gensalt(12);
        String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt(salt));

        Role role = ModelController.getRole().findRole(RoleEnum.CLIENT.getId());

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashPassword);
        user.setRole(role);
        user.setCreatedAt(new Date());

        try {
            ModelController.getUser().create(user);
            Response.outputData(response, 200, user);
            return;
        } catch (Exception ex) {
            CustomLogger.errorThrow(RegisterController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
}