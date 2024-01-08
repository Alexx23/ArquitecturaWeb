package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Ticket;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/ticket/*")
public class TicketController extends HttpServlet {

    public TicketController() {
        super();
    }

    /*
    /ticket -> Ver lista con todas los tickets
    /ticket/{id} -> Ver información del ticket con id = {id}
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String ticketIdStr = Request.getURLValue(request);
        
        if (ticketIdStr == null) {
            Map<String, Integer> integers = null;
            try {
                integers = Request.validateInteger(request, "page");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;            
            }
            int actualPage = integers.get("page") != null ? integers.get("page") : 1;
            Response.outputData(response, 200, PaginationHelper.getPaginated(Ticket.class, actualPage, null), 5);
            return;
        }
        
        int ticketId = Integer.parseInt(ticketIdStr);
        Ticket ticket = ModelController.getTicket().findTicket(ticketId);
        if (ticket == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el ticket solicitado");
            return;
        }
        Response.outputData(response, 200, ticket);
        
    }
   
}