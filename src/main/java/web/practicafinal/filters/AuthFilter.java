package web.practicafinal.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
@WebFilter(filterName="auth", urlPatterns = {
    "/logout", 
    "/actor/*", "/ageclassification/*", "/director/*", "/distributor/*", "/genre/*", "/movie/*", "/nationality/*", "/room/*", "/session/*", "/ticket/*", "/user/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Comprobar que existe la sesión
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            Response.outputMessage(httpResponse, 401, "Sesión no iniciada.");
            return;
        }
        
        // Comprobar que existe el atributo 'user_id' en la sesión
        Object userIdObj = session.getAttribute("user_id");
        if (userIdObj == null) {
            Response.outputMessage(httpResponse, 401, "Sesión no válida.");
            return;
        }
        
        // Obtener instancia del usuario
        int userId = (int) userIdObj;
        User user = ModelController.getUser().findUser(userId);
        if (user == null) {
            Response.outputMessage(httpResponse, 401, "Sesión con usuario no válido.");
            return;
        }
        
        // Pasar la instancia del usuario como atributo para poder usarlo posteriormente
        request.setAttribute("user_session", user);
        chain.doFilter(request, response);
    }
    
}
