package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Size;

/**
 *
 * @author Alex
 */
public class DistributorUpdateDTO {
    
    @Size(min = 1, max = 255)
    private String name;
    
    public DistributorUpdateDTO() {}

    public DistributorUpdateDTO(String name) {
        this.name = name;
    }


    
}
