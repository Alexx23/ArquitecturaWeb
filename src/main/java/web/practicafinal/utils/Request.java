package web.practicafinal.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import web.practicafinal.enums.RequestScope;
import web.practicafinal.exceptions.ValidateException;

/**
 *
 * @author Alex
 */
public class Request {
    
    public static String getURLValue(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) return null;
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length <= 1) return null;
        return pathParts[1];
    }
    
    public static Map<String, String> validate(RequestScope scope, HttpServletRequest request, String... parameters) throws ValidateException {
        Map<String, String> result = new HashMap<>();

        String emailRegex = "^(.+)@(.+)$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        for (String parameter : parameters) {
            String value = request.getParameter(parameter);

            if (value == null || value == "") {
                throw new ValidateException("El campo "+parameter+" no es válido.");
            }

            if (parameter == "password"
                    && value.length() < 8) {

                if (!scope.equals(RequestScope.LOGIN)) {
                    throw new ValidateException("La contraseña debe tener más de 8 caracteres.");
                }

            }

            if (parameter == "password_confirmation") {
                if (request.getParameter("password") == null || request.getParameter("password") == "") {
                    throw new ValidateException("El campo password no es válido.");
                }

                if (!value.equals(request.getParameter("password"))) {
                    throw new ValidateException("Las contraseñas no coinciden.");
                }

            }

            if (parameter == "email") {
                Matcher matcher = emailPattern.matcher(value);
                if (!matcher.matches()) {
                    throw new ValidateException("No se ha introducido un email válido.");
                }
                value = value.toLowerCase();
            }

            if (parameter == "username") {
                value = value.toLowerCase();
            }
            
            if (parameter == "duration") {
                if (Integer.valueOf(value) < 0) {
                    throw new ValidateException("La duración debe ser positiva.");
                }
            }
            
            if (parameter == "year") {
                if (Integer.valueOf(value) < 0) {
                    throw new ValidateException("El año debe ser positivo.");
                }
            }

            result.put(parameter, value);

        }

        return result;
    }
    
}
