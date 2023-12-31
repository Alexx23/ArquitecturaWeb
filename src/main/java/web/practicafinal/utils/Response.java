package web.practicafinal.utils;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Alex
 */
public class Response {

    static class DeepFieldFilter extends SimpleBeanPropertyFilter {
        private final int maxDepth;

        public DeepFieldFilter(int maxDepth) {
            super();
            this.maxDepth = maxDepth;
        }

        private int calcDepth(PropertyWriter writer, JsonGenerator jgen) {
            JsonStreamContext sc = jgen.getOutputContext();
            int depth = -1;
            while (sc != null) {
                sc = sc.getParent();
                depth++;
            }
            return depth;
        }

        @Override
        public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider provider, PropertyWriter writer) throws Exception {
            int depth = calcDepth(writer, gen);
            if (depth <= maxDepth) {
                writer.serializeAsField(pojo, gen, provider);
            }
            // Comentar si no se desea que aparezca {}
            /*else {
                writer.serializeAsOmittedField(pojo, gen, provider);
            }*/
        }

    }
    
    @JsonFilter("depth_3")
    @JsonIgnoreProperties({ "password", "cvv", "cardNumber" })
    interface FilterMixin {
    }
        
    public static void outputData(HttpServletResponse response, int status, Object payload) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);

        try {
            if (payload != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.addMixIn(Object.class, FilterMixin.class);
                SimpleFilterProvider depthFilter = new SimpleFilterProvider().addFilter("depth_3", new DeepFieldFilter(3));
                objectMapper.setFilterProvider(depthFilter);
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                String result = objectMapper.writeValueAsString(payload);
                
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
