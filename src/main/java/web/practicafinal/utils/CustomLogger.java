package web.practicafinal.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class CustomLogger {
    
    public static void info(String message) {
        Logger.getGlobal().log(Level.INFO, message);
    }
    
    public static void warn(String message) {
        Logger.getGlobal().log(Level.WARNING, message);
    }
    
    public static void error(String className, String message) {
        Logger.getLogger(className).log(Level.SEVERE, message);
    }
    
    public static void errorThrow(String className, Exception exception) {
        Logger.getLogger(className).log(Level.SEVERE, null, exception);
    }
    
}
