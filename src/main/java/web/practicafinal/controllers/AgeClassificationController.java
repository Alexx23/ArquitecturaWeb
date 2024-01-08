package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import web.practicafinal.models.AgeClassification;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.utils.Response;

@WebServlet("/ageclassification/*")
public class AgeClassificationController extends HttpServlet {

    public AgeClassificationController() {
        super();
    }

    /*
    /ageclassification -> Ver lista con todas las clasificaciones
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        List<AgeClassification> ageClassifications = ModelController.getAgeClassification().findAgeClassificationEntities();
        Response.outputData(response, 200, ageClassifications);
        return;
        
        
    }
    
}