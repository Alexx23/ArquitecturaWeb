package web.practicafinal.models.seeders;

import web.practicafinal.enums.AgeClassificationEnum;
import web.practicafinal.models.AgeClassification;
import web.practicafinal.models.controllers.AgeClassificationJpaController;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.utils.CustomLogger;

/**
 *
 * @author Alex
 */
public class AgeClassificationSeeder {

    public static void seed() {
        
        AgeClassificationEnum[] ageClassificationEnums = AgeClassificationEnum.values();
        
        for (AgeClassificationEnum ace : ageClassificationEnums) {
            AgeClassification ageClassification = ModelController.getAgeClassification().findAgeClassification(ace.getId());
            if (ageClassification == null) {
                AgeClassification newAgeClassification = new AgeClassification();
                newAgeClassification.setId(ace.getId());
                newAgeClassification.setName(ace.getName());
                newAgeClassification.setAge(ace.getAge());
                try {
                    ModelController.getAgeClassification().create(newAgeClassification);
                    CustomLogger.info("Nueva age_classification creada en la base de datos: "+newAgeClassification.getName()+" con id "+newAgeClassification.getId());
                } catch (Exception ex) {
                    CustomLogger.errorThrow(RoleSeeder.class.getName(), ex);
                }
            }
        }
        
    }
    
}
