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
import java.util.logging.Level;
import java.util.logging.Logger;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
/*
@WebFilter(filterName="auth", urlPatterns = {
    "/logout", 
    "/actor/*", "/ageclassification/*", "/director/*", "/distributor/*", "/genre/*", "/movie/*", "/nationality/*", "/room/*", "/session/*", "/ticket/*", "/user/*"})
*/
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
        
        // Comprobar validez del usuario
        try {
            Request.getUser(httpRequest);
        } catch (SessionException ex) {
            Response.outputMessage(httpResponse, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        chain.doFilter(request, response);
    }
    
}
