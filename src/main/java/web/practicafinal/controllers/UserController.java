package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/user/*")
public class UserController extends HttpServlet {

    public UserController() {
        super();
    }

    /*
    /user -> Ver lista con todas los users
    /user/{id} -> Ver información del user con id = {id}
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String userIdStr = Request.getURLValue(request);
        
        if (userIdStr == null) {
            Map<String, Integer> integers = null;
            try {
                integers = Request.validateInteger(request, "page");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;            
            }
            int actualPage = integers.get("page") != null ? integers.get("page") : 1;
            Response.outputData(response, 200, PaginationHelper.getPaginated(User.class, actualPage, request.getParameter("name")), 4);
            return;
        }
        
        int userId = Integer.parseInt(userIdStr);
        User user = ModelController.getUser().findUser(userId);
        if (user == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el usuario solicitado");
            return;
        }
        Response.outputData(response, 200, user);
        
    }
    
}