package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import web.practicafinal.models.Ticket;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
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
            List<Ticket> tickets = ModelController.getTicket().findTicketEntities();
            Response.outputData(response, 200, tickets);
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