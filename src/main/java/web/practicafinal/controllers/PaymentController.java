package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import web.practicafinal.controllers.validations.TicketCreateDTO;
import web.practicafinal.controllers.validations.TicketItemCreateDTO;
import web.practicafinal.enums.PriceEnum;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.UnauthorizedException;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Card;
import web.practicafinal.models.Payment;
import web.practicafinal.models.Session;
import web.practicafinal.models.Ticket;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.models.helpers.PaymentHelper;
import web.practicafinal.models.helpers.TicketHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.CypherUtils;
import web.practicafinal.utils.Middleware;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/payment/*")
public class PaymentController extends HttpServlet {

    public PaymentController() {
        super();
    }
    
    
    /*
    /payment -> Ver lista paginada con todas los pagos
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA SOLO PARA ADMINS
        //////////////////////
        try {
            Middleware.adminRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        } catch (UnauthorizedException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;        
        }
        
        Map<String, Integer> integers = null;
        try {
            integers = Request.validateInteger(request, "page");
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;            
        }
        int actualPage = integers.get("page") != null ? integers.get("page") : 1;
        // En este caso solicito más profundidad en la respuesta para poder mostrar más información al admin
        Response.outputData(response, 200, PaginationHelper.getPaginated(Payment.class, actualPage, null), 4);
        return;
    }
    
    /*
    /payment -> Crear un nuevo pago y genera tickets
    */
    // NOTA: Esta es la ruta más compleja de todas
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA PARA CLIENTES
        //////////////////////
        
        try {
            Middleware.authRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Validar parámetros de la solicitud
        Map<String, Integer> integers = null;
        try {
            integers = Request.validateInteger(request, "card_id", "session_id");
            
            TicketCreateDTO ticketCreateDTO = new TicketCreateDTO(integers.get("card_id"), integers.get("session_id"));

            Request.validateViolations(ticketCreateDTO);
            
        } catch (ValidateException ex) {
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
        
        // Se compone de estos pasos
        // 1. Comprobaciones
        // 2. Crear pago
        // 3. Crear tickets
        
        // COMPROBAR
        ///////////////
        
        List<TicketItemCreateDTO> requestedTickets = new ArrayList<TicketItemCreateDTO>();
        // La solicitud consiste en una lista de tickets
        // Leer las ids de los actores y guardarlas en una lista de enteros
        boolean continueReading = true;
        int count = 0;
        while (continueReading) {
            String parameterStr = request.getParameter("seats["+count+"]");
            if (parameterStr != null) {
                if (parameterStr.split(":").length != 2) continueReading = false;
                String depthStr = parameterStr.split(":")[0];
                String seatStr = parameterStr.split(":")[1];
                TicketItemCreateDTO ticket = new TicketItemCreateDTO(Short.parseShort(depthStr), Short.parseShort(seatStr));
                requestedTickets.add(ticket);
                
                count++;
            } else {
                continueReading = false;
            }
        }
        
        if (requestedTickets.isEmpty()) {
            Response.outputMessage(response, 400, "No se ha solicitado la compra de ningún ticket.");
            return;
        }
        
        Session session = ModelController.getSession().findSession(integers.get("session_id"));

        // Comprobar que los tickets no tienen butacas ya ocupadas
        boolean occupied = false; // Se contempla desde un inicio que no hay sitios ocupados
        List<Ticket> sessionTickets =  session.getTicketList();
        for (Ticket sessionTicket : sessionTickets) {
            for (TicketItemCreateDTO requestedTicket : requestedTickets) {
                if (occupied) break;
                if (sessionTicket.getDepth() == requestedTicket.getDepth()
                        && sessionTicket.getSeat() == requestedTicket.getSeat()) {
                    occupied = true;
                }
            }
        }
        if (occupied) {
            Response.outputMessage(response, 400, "Alguna de las butacas seleccionadas se encuentra ocupada por otra persona.");
            return;
        }

        
        // CREAR PAGO
        ///////////////
        
        // Procesar precios
        float unitPrice = PriceEnum.NORMAL.getPrice();
        float totalPrice = requestedTickets.size() * unitPrice;

        // Comprobar tarjeta
        Card card = ModelController.getCard().findCard(integers.get("card_id"));
        if (card.getUser().getId() != userSession.getId()) {
            Response.outputMessage(response, 400, "No eres el dueño de esa tarjeta.");
            return;
        }
        
        // Guardar fecha de creación
        Date createdDate = new Date();
        
        // Obtener referencia única para el pago
        String uniqueReference = PaymentHelper.generateUniqueReference();
        
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal(totalPrice));
        payment.setCardTitle(card.getTitle());
        payment.setCardNumber(card.getCardNumber());
        payment.setReference(uniqueReference);
        payment.setUser(userSession);
        payment.setCreatedAt(createdDate);

        try {
            ModelController.getPayment().create(payment);
        } catch (Exception ex) {
            CustomLogger.errorThrow(RoomController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }

        
        // CREAR TICKETS
        ///////////////
    
        // Obtener el pago creado de la base de datos
        // porque tendrá ID
        payment = PaymentHelper.getPaymentByReference(uniqueReference);
        
        // Crear los tickets
        for (TicketItemCreateDTO requestedTicket : requestedTickets) {
            
            // Obtener código único para el ticket
            String uniqueCode = TicketHelper.generateUniqueCode();
            
            Ticket ticket = new Ticket();
            ticket.setDepth(requestedTicket.getDepth());
            ticket.setSeat(requestedTicket.getSeat());
            ticket.setCode(uniqueCode);
            ticket.setCreatedAt(createdDate);
            ticket.setSession(session);
            ticket.setUser(userSession);
            ticket.setPayment(payment);
            
            try {
                ModelController.getTicket().create(ticket);
            } catch (Exception ex) {
                CustomLogger.errorThrow(RegisterController.class.getName(), ex);
                Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
                return;
            }
        }
        
        Response.outputData(response, 200, "Tickets creados correctamente.");
        return;
        
    }
 
   
}