package web.practicafinal.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import web.practicafinal.enums.RoleEnum;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.models.User;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
@WebFilter(filterName="admin", urlPatterns = {
    "/actor/*", "/ageclassification/*", "/director/*", "/distributor/*", "/genre/*", "/movie/*", "/nationality/*", "/room/*", "/session/*", "/ticket/*", "/user/*"})
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Obtener usuario
        User userSession;
        try {
            userSession = Request.getUser(httpRequest);
        } catch (SessionException ex) {
            Response.outputMessage(httpResponse, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Comprobar role del usuario
        if (userSession.getRole().getId() != RoleEnum.ADMIN.getId()) {
            Response.outputMessage(httpResponse, 403, "Acceso no autorizado");
            return;
        }

        chain.doFilter(request, response); 
    }
    
}
