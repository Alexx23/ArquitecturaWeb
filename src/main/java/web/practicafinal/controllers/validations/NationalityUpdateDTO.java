package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Size;

/**
 *
 * @author Alex
 */
public class NationalityUpdateDTO {
    
    @Size(min = 1, max = 255)
    private String name;
    
    public NationalityUpdateDTO() {}

    public NationalityUpdateDTO(String name) {
        this.name = name;
    }


    
}
