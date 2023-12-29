package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import web.practicafinal.models.Actor;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.utils.CustomLogger;

/**
 *
 * @author Alex
 */
@WebServlet("/test")
public class TestController extends HttpServlet {

    public TestController() {
        super();
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        Actor actor = new Actor();
        actor.setName("test");
        try {
            ModelController.getActor().create(actor);
            CustomLogger.info("Actor creado");
        } catch (Exception ex) {
            Logger.getLogger(TestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Test</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet en " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
