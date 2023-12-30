package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import web.practicafinal.models.Actor;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.Response;

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
        Actor actor = new Actor();
        actor.setName("test");
        try {
            ModelController.getActor().create(actor);
            Response.outputMessage(response, 200, "Actor creado.");
        } catch (Exception ex) {
            CustomLogger.errorThrow(TestController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
        }
    }
}
