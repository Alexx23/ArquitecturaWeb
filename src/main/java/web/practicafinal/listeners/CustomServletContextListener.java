package web.practicafinal.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import javax.naming.NamingException;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.seeders.AgeClassificationSeeder;
import web.practicafinal.models.seeders.RoleSeeder;
import web.practicafinal.utils.CustomLogger;

/**
 *
 * @author Alex
 */
@WebListener
public class CustomServletContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ModelController.init();
        } catch (NamingException ex) {
            CustomLogger.errorThrow(CustomServletContextListener.class.getName(), ex);
        }
        RoleSeeder.seed();
        AgeClassificationSeeder.seed();
        CustomLogger.info("Aplicacion web arrancada.");
    }
 
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ModelController.destroy();
        } catch (NamingException ex) {
            CustomLogger.errorThrow(CustomServletContextListener.class.getName(), ex);
        }
        CustomLogger.info("Aplicacion web parada.");
    }
}
