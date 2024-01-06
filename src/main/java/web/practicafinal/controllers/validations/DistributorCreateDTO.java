package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author Alex
 */
public class DistributorCreateDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    
    public DistributorCreateDTO() {}

    public DistributorCreateDTO(String name) {
        this.name = name;
    }


    
}
