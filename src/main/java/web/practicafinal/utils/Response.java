package web.practicafinal.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Alex
 */
public class Response {

    public static void outputData(HttpServletResponse response, int status, Object payload) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);

        try {
            if (payload != null) {
                ObjectMapper basicMapper = new ObjectMapper();
                String result = basicMapper.writeValueAsString(payload);
                
                PrintWriter out = response.getWriter();
                out.print(result);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public static void outputMessage(HttpServletResponse response, int status, String message) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);

        try {
            JsonObject json = new JsonObject();
            json.addProperty("message", message);
            
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
