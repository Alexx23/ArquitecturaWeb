package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCrypt;
import web.practicafinal.enums.RequestScope;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.UserJpaController;
import web.practicafinal.models.helpers.UserHelper;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
@WebServlet("/login")
public class LoginController extends HttpServlet {
    
    private static UserJpaController userJpaController = null;

    public LoginController() {
        super();
    }
    
    @Override
    public void init() {
        userJpaController = ModelController.getUser();
    }
    
    /*
    /login -> Iniciar sesión
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Map<String, String> validatedRequest = Request.validate(RequestScope.LOGIN, request, 
                    "username", "password");
           
            String username = validatedRequest.get("username");
            String password = validatedRequest.get("password");
            
            User user = UserHelper.getUserByUsernameEmail(username);
            if (user == null) {
                Response.outputMessage(response, 400, "Usuario o contraseña incorrectos.");
                return;
            }
            
            boolean passSuccess = BCrypt.checkpw(password, user.getPassword());
            if (!passSuccess) {
                Response.outputMessage(response, 400, "Usuario o contraseña incorrectos.");
                return;
            }
            
            HttpSession session = request.getSession();
            session.setAttribute("user_id", user.getId());
            
            Response.outputData(response, 200, user, true);
        
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
    }
}