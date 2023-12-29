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
import web.practicafinal.models.controllers.UserJpaController;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
@WebFilter(filterName="auth", urlPatterns = {"/logout", "/movie/*", "/room/*"})
public class AuthFilter implements Filter {
    
    private static UserJpaController userJpaController = null;
    
    @Override
    public void init(FilterConfig filterConfig) {
        userJpaController = ModelController.getUser();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            Response.outputMessage(httpResponse, 401, "Sesión no iniciada.");
            return;
        }
        
        Object userIdObj = session.getAttribute("user_id");
        if (userIdObj == null) {
            Response.outputMessage(httpResponse, 401, "Sesión no válida.");
            return;
        }
        
        int userId = (int) userIdObj;
        User user = userJpaController.findUser(userId);
        if (user == null) {
            Response.outputMessage(httpResponse, 401, "Sesión con usuario no válido.");
            return;
        }
        
        request.setAttribute("user_session", user);
        chain.doFilter(request, response);
    }
    
}
