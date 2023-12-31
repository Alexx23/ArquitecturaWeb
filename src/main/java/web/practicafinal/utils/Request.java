package web.practicafinal.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import web.practicafinal.exceptions.ValidateException;

/**
 *
 * @author Alex
 */
public class Request {
    
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    private static final SimpleDateFormat dateFormatISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //Formato ISO. Ejemplo: 2011-10-05T14:48:00.000Z
    
    public static String getURLValue(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) return null;
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length <= 1) return null;
        return pathParts[1];
    }
    
    /*
        Valida que si existen estos parámetros en la request, sean Integer
        Si existen, se devolverán como Integer. Si no existen, se devolverán como null
    */
    public static Map<String, Integer> validateInteger(HttpServletRequest request, String... parameters) throws ValidateException {
        Map<String, Integer> result = new HashMap<>();
        for (String parameter : parameters) {
            String value = request.getParameter(parameter);
            if (value == null) {
                result.put(parameter, null);
                continue;
            }
            try {
                Integer integer_ = Integer.parseInt(value);
                result.put(parameter, integer_);
            } catch (NumberFormatException e) {
                throw new ValidateException("El campo "+parameter+" debe ser un número entero.");
            }
        }
        return result;
    }
    
    /*
        Valida que si existen estos parámetros en la request, sean Short
        Si existen, se devolverán como Short. Si no existen, se devolverán como null
    */
    public static Map<String, Short> validateShort(HttpServletRequest request, String... parameters) throws ValidateException {
        Map<String, Short> result = new HashMap<>();
        for (String parameter : parameters) {
            String value = request.getParameter(parameter);
            if (value == null) {
                result.put(parameter, null);
                continue;
            }
            try {
                Short short_ = Short.parseShort(value);
                result.put(parameter, short_);
            } catch (NumberFormatException e) {
                throw new ValidateException("El campo "+parameter+" debe ser un número entero.");
            }
        }
        return result;
    }
    
    /*
        Valida que si existen estos parámetros en la request, sean Date
        Si existen, se devolverán como Date. Si no existen, se devolverán como null
    */
    public static Map<String, Date> validateDate(HttpServletRequest request, String... parameters) throws ValidateException {
        Map<String, Date> result = new HashMap<>();
        for (String parameter : parameters) {
            String value = request.getParameter(parameter);
            if (value == null) {
                result.put(parameter, null);
                continue;
            }
            try {
                Date date_ = dateFormatISO.parse(value);
                result.put(parameter, date_);
            } catch (ParseException e) {
                throw new ValidateException("El campo "+parameter+" debe ser una fecha.");
            }
        }
        return result;
    }
    
    public static <T> void validateViolations(T object) throws ValidateException {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        if (!violations.isEmpty()) {
            ConstraintViolation<T> firstViolation = violations.iterator().next();
            throw new ValidateException("El campo "+firstViolation.getPropertyPath()+" "+firstViolation.getMessage()+".");
        }
    }
    
}
