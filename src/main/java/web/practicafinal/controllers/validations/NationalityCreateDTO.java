package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.UniqueNationalityName;

/**
 *
 * @author Alex
 */
public class NationalityCreateDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    @UniqueNationalityName
    private String name;
    
    public NationalityCreateDTO() {}

    public NationalityCreateDTO(String name) {
        this.name = name;
    }


    
}
