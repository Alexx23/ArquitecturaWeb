package web.practicafinal.models.seeders;

import web.practicafinal.enums.RoleEnum;
import web.practicafinal.models.Role;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.RoleJpaController;
import web.practicafinal.utils.CustomLogger;

/**
 *
 * @author Alex
 */
public class RoleSeeder {
    private static RoleJpaController roleJpaController = ModelController.getRole();
    
    public static void seed() {
        
        RoleEnum[] roleEnums = RoleEnum.values();
        
        for (RoleEnum re : roleEnums) {
            Role role = roleJpaController.findRole(re.getId());
            if (role == null) {
                Role newRole = new Role();
                newRole.setId(re.getId());
                newRole.setName(re.name().toLowerCase());
                try {
                    roleJpaController.create(newRole);
                    CustomLogger.info("Nuevo rol creado en la base de datos: "+newRole.getName()+" con id "+newRole.getId());
                } catch (Exception ex) {
                    CustomLogger.errorThrow(RoleSeeder.class.getName(), ex);
                }
            }
        }
        
    }
    
}
