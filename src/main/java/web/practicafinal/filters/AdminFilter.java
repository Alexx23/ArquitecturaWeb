package web.practicafinal.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import web.practicafinal.enums.RoleEnum;
import web.practicafinal.models.User;
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
        
        // Obtener el usuario (nunca será nulo porque antes se ha ejecutado el filtro 'auth')
        User user = (User) request.getAttribute("user_session");
        
        if (user.getRole().getId() != RoleEnum.ADMIN.getId()) {
            Response.outputMessage(httpResponse, 403, "Acceso no autorizado");
            return;
        }

        chain.doFilter(request, response); 
    }
    
}
