package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.UniqueGenreName;

/**
 *
 * @author Alex
 */
public class GenreCreateDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    @UniqueGenreName
    private String name;
    
    public GenreCreateDTO() {}

    public GenreCreateDTO(String name) {
        this.name = name;
    }


    
}
