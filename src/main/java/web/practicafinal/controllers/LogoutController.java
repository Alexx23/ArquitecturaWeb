package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.UserJpaController;
import web.practicafinal.utils.Response;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    public LogoutController() {
        super();
    }
    
    /*
    /logout -> Cerrar sesión
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            HttpSession session = request.getSession(false);
            session.removeAttribute("user_id");
            session.invalidate();
            
            Response.outputMessage(response, 200, "Sesión cerrada correctamente.");

    }
}