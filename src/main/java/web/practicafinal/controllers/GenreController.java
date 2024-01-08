package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import web.practicafinal.controllers.validations.GenreCreateDTO;
import web.practicafinal.controllers.validations.GenreUpdateDTO;
import web.practicafinal.exceptions.ValidateException;
import web.practicafinal.models.Genre;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.exceptions.RollbackFailureException;
import web.practicafinal.models.helpers.GenreHelper;
import web.practicafinal.models.helpers.PaginationHelper;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.InstanceConverter;
import web.practicafinal.utils.Request;
import web.practicafinal.utils.Response;

@WebServlet("/genre/*")
public class GenreController extends HttpServlet {

    public GenreController() {
        super();
    }

    /*
    /genre -> Ver lista con todos los géneros
    /genre/{id} -> Ver información del género con id = {id}
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String genreIdStr = Request.getURLValue(request);
        
        if (genreIdStr == null) {
            Map<String, Integer> integers = null;
            try {
                integers = Request.validateInteger(request, "page");
            } catch (ValidateException ex) {
                Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
                return;            
            }
            int actualPage = integers.get("page") != null ? integers.get("page") : 1;
            Response.outputData(response, 200, PaginationHelper.getPaginated(Genre.class, actualPage, request.getParameter("name")), 4);
            return;
        }
        
        if (genreIdStr.equalsIgnoreCase("all")) {
            List<Genre> genres = ModelController.getGenre().findGenreEntities();
            Response.outputData(response, 200, genres);
            return;
        }
        
        int genreId = Integer.parseInt(genreIdStr);
        Genre genre = ModelController.getGenre().findGenre(genreId);
        if (genre == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el género solicitado");
            return;
        }
        Response.outputData(response, 200, genre);
        
    }
    
    /*
    /genre -> Crear género
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        try {
            GenreCreateDTO genreCreateDTO = new GenreCreateDTO(request.getParameter("name"));

            Request.validateViolations(genreCreateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }
        
        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        Genre genre = new Genre();
        genre.setName(request.getParameter("name"));

        try {
            ModelController.getGenre().create(genre);
            Response.outputData(response, 200, genre);
        } catch (Exception ex) {
            CustomLogger.errorThrow(GenreController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
        
    }
    
    
    /*
    /genre/{id} -> Actualizar género con id = {id}
    */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Validar parámetros de la solicitud
        GenreUpdateDTO genreUpdateDTO = null;
        try {
            genreUpdateDTO = new GenreUpdateDTO(request.getParameter("name"));

            Request.validateViolations(genreUpdateDTO);
            
        } catch (ValidateException ex) {
            Response.outputMessage(response, ex.getHttpErrorCode(), ex.getMessage());
            return;
        }

        // Parámetros validados. Hacer comprobaciones y operaciones correspondientes
        String genreIdStr = Request.getURLValue(request);
        
        if (genreIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ningún género.");
            return;
        }
        
        int genreId = Integer.parseInt(genreIdStr);
        
        Genre genre = ModelController.getGenre().findGenre(genreId);
        if (genre == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el género solicitado");
            return;
        }
        
        // Comprobar que el nombre del género sea único
        if (request.getParameter("name") != null && 
                !genre.getName().equalsIgnoreCase(request.getParameter("name")) &&
                GenreHelper.getGenreByName(request.getParameter("name")) != null) {
            Response.outputMessage(response, 404, "Ese nombre ya está siendo utilizado por otro género");
            return;
        }

        InstanceConverter.updateInstance(Genre.class, genre, GenreUpdateDTO.class, genreUpdateDTO);
        
        try {
            ModelController.getGenre().edit(genre);
            Response.outputData(response, 200, genre);
        } catch (Exception ex) {
            CustomLogger.errorThrow(GenreController.class.getName(), ex);
            Response.outputMessage(response, 500, "Ha ocurrido un error interno.");
            return;
        }
    }
    
    /*
    /genre/{id} -> Eliminar género con id = {id}
    */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String genreIdStr = Request.getURLValue(request);
        
        if (genreIdStr == null) {
            Response.outputMessage(response, 400, "No se ha seleccionado ningún género.");
            return;
        }
        
        int genreId = Integer.parseInt(genreIdStr);
        
        Genre genre = ModelController.getGenre().findGenre(genreId);
        if (genre == null) {
            Response.outputMessage(response, 404, "No se ha encontrado el género solicitado");
            return;
        }
        
        try {
            ModelController.getGenre().destroy(genreId);
            Response.outputMessage(response, 200, "Género eliminado correctamente");
        } catch (RollbackFailureException ex) {
            CustomLogger.errorThrow(GenreController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el género");
        } catch (Exception ex) {
            CustomLogger.errorThrow(GenreController.class.getName(), ex);
            Response.outputMessage(response, 500, "No se ha podido eliminar el género");
        }
        
    }
    
}