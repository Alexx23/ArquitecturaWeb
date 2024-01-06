package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.UniqueNationalityName;

/**
 *
 * @author Alex
 */
public class NationalityUpdateDTO {
    
    @Size(min = 1, max = 255)
    @UniqueNationalityName
    private String name;
    
    public NationalityUpdateDTO() {}

    public NationalityUpdateDTO(String name) {
        this.name = name;
    }


    
}
