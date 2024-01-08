package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.models.User;
import web.practicafinal.utils.Middleware;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/usersession/*")
public class UserSessionController extends HttpServlet {

    public UserSessionController() {
        super();
    }

    /*
    /usersession -> Ver usuario que tiene la sesión iniciada
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA SOLO CLIENTES
        //////////////////////
        try {
            Middleware.authRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        User userSession;
        try {
            userSession = Request.getUser(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        Response.outputData(response, 200, userSession);
        
    }
    
}