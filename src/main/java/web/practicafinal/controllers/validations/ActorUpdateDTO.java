package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author Alex
 */
public class ActorUpdateDTO {
    
    @Size(min = 1, max = 255)
    private String name;
    
    public ActorUpdateDTO() {}

    public ActorUpdateDTO(String name) {
        this.name = name;
    }


    
}
