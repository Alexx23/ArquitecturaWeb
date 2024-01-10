package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCrypt;
import web.practicafinal.controllers.validations.CardCreateDTO;
import web.practicafinal.controllers.validations.PasswordCreateDTO;
import web.practicafinal.controllers.validations.UserUpdateDTO;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Card;
import web.practicafinal.models.Ticket;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.helpers.CardHelper;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.models.helpers.UserHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.DateUtils;
import web.practicafinal.utils.InstanceConverter;
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
        
        String parameter = Request.getURLValue(request);
        
        // Si está llamado a /usersession
        if (parameter == null) {
        
            User userSession;
            try {
                userSession = Request.getUser(request);
            } catch (SessionException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;
            }

            Response.outputData(response, 200, userSession);
            return;
        
        }
        
        // Si está llamado a /usersession/ticket
        if (parameter.equalsIgnoreCase("ticket")) {
            doGetTicket(request, response);
            return;
        }
        
        Response.outputMessage(response, 404, "Ruta no válida.");
    }
    
    
    
    /*
    /usersession/password -> Crear nueva contraseña para el usuario que tiene la sesión iniciada
    /usersession/card -> Crear nueva tarjeta para el usuario que tiene la sesión iniciada
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA SOLO CLIENTES
        //////////////////////
        try {
            Middleware.authRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Si está llamado a /usersession/password
        if (request.getRequestURI().endsWith("/password")) {
            doPostPassword(request, response);
            return;
        }
        
        // Si está llamado a /usersession/card
        if (request.getRequestURI().endsWith("/card")) {
            doPostCard(request, response);
            return;
        }

        Response.outputMessage(response, 404, "Ruta no válida");
    }
    
    /*
    /usersession -> Actualizar usuario que tiene la sesión iniciada
    */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA SOLO CLIENTES
        //////////////////////
        try {
            Middleware.authRoute(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Validar parámetros de la solicitud
        UserUpdateDTO userUpdateDTO = null;
        try {
            userUpdateDTO = new UserUpdateDTO(request.getParameter("name"), request.getParameter("username"), request.getParameter("email"));

            Request.validateViolations(userUpdateDTO);
            
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
        
        // Comprobar que el username sea único
        if (request.getParameter("username") != null && 
                !userSession.getUsername().equalsIgnoreCase(request.getParameter("username")) &&
                UserHelper.getUserByUsername(request.getParameter("name")) != null) {
            Response.outputMessage(response, 400, "Ese nombre de usuario ya está siendo utilizado por otro usuario");
            return;
        }
        
        // Comprobar que el username sea único
        if (request.getParameter("email") != null && 
                !userSession.getEmail().equalsIgnoreCase(request.getParameter("email")) &&
                UserHelper.getUserByEmail(request.getParameter("email")) != null) {
            Response.outputMessage(response, 400, "Ese email ya está siendo utilizado por otro usuario");
            return;
        }
        
        InstanceConverter.updateInstance(User.class, userSession, UserUpdateDTO.class, userUpdateDTO);
        
        try {
            ModelController.getUser().edit(userSession);
            Response.outputData(response, 200, userSession);
        } catch (Exception ex) {
            CustomLogger.errorThrow(UserSessionController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    
    /*
    /usersession/card -> Eliminar tarjeta del usuario que tiene la sesión iniciada
    */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String path = Request.getURLValue(request);
        
        // Si está llamado a /usersession/card/{id}
        if (path.equalsIgnoreCase("card")) {
            doDeleteCard(request, response);
            return;
        }

        Response.outputMessage(response, 404, "Ruta no válida");
        
    }
    
    private void doPostPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        PasswordCreateDTO passwordCreateDTO = null;
        try {
            passwordCreateDTO = new PasswordCreateDTO(request.getParameter("current_password"), request.getParameter("new_password"), request.getParameter("new_password_confirmation"));

            Request.validateViolations(passwordCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        String currentPassword = request.getParameter("current_password");
        String newPassword = request.getParameter("new_password");
        String newPasswordConfirmation = request.getParameter("new_password_confirmation");
        
        User userSession;
        try {
            userSession = Request.getUser(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        boolean passSuccess = BCrypt.checkpw(currentPassword, userSession.getPassword());
        if (!passSuccess) {
            Response.outputMessage(response, 400, "La contraseña actual es incorrecta.");
            return;
        }
        
        if (!newPassword.equals(newPasswordConfirmation)) {
            Response.outputMessage(response, 400, "Las contraseñas no coinciden.");
            return;
        }
        
        String salt = BCrypt.gensalt(12);
        String hashPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(salt));
        
        userSession.setPassword(hashPassword);
        
        try {
            ModelController.getUser().edit(userSession);
            Response.outputMessage(response, 200, "Contraseña cambiada correctamente");
        } catch (Exception ex) {
            CustomLogger.errorThrow(MovieController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    private void doPostCard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        CardCreateDTO cardCreateDTO = null;
        Map<String, Long> longs = null;
        Map<String, Date> dates = null;
        Map<String, Integer> integers = null;
        try {
            longs = Request.validateLong(request, "card_number");
            dates = Request.validateDate(request, "expiration");
            integers = Request.validateInteger(request, "cvv");
            
            cardCreateDTO = new CardCreateDTO(request.getParameter("title"), longs.get("card_number"), dates.get("expiration"), integers.get("cvv"));

            Request.validateViolations(cardCreateDTO);
            
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
        
        String encryptedCvv = CardHelper.encryptCvv(integers.get("cvv").toString());
        
        Card card = new Card();
        card.setTitle(request.getParameter("title"));
        card.setCardNumber(longs.get("card_number"));
        card.setExpiration(DateUtils.truncateDay(dates.get("expiration")));
        card.setCvv(encryptedCvv);
        card.setUser(userSession);
        card.setCreatedAt(new Date());
        
        try {
            ModelController.getCard().create(card);
            Response.outputData(response, 200, card);
        } catch (Exception ex) {
            CustomLogger.errorThrow(MovieController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }
    
    private void doDeleteCard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String cardIdStr = Request.getSecondURLValue(request);
        if (cardIdStr == null) {
            Response.outputMessage(response, 404, "No se ha especificado una tarjeta");
            return;
        }
        int cardId = Integer.parseInt(cardIdStr);
        Card card = ModelController.getCard().findCard(cardId);
        if (card == null) {
            Response.outputMessage(response, 404, "No se ha encontrado la tarjeta solicitada");
            return;
        }

        User userSession;
        try {
            userSession = Request.getUser(request);
        } catch (SessionException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        if (card.getUser().getId() != userSession.getId()) {
            Response.outputMessage(response, 400, "No eres el dueño de esa tarjeta.");
            return;
        }

        try {
            ModelController.getCard().destroy(cardId);
            Response.outputData(response, 200, "Tarjeta eliminada correctamente");
        } catch (Exception ex) {
            CustomLogger.errorThrow(MovieController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }
    
    
    private void doGetTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        User userSession;
        try {
            userSession = Request.getUser(request);
        } catch (SessionException ex) {
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
        Map<String, Object> mapParameters = new HashMap<>();
        mapParameters.put("user", userSession);
        Response.outputData(response, 200, PaginationHelper.getPaginatedWithFilters(Ticket.class, actualPage, mapParameters), 5);
    
    }
    
}