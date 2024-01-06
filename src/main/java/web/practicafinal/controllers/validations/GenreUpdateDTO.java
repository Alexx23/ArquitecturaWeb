package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.UniqueGenreName;

/**
 *
 * @author Alex
 */
public class GenreUpdateDTO {
    
    @Size(min = 1, max = 255)
    @UniqueGenreName
    private String name;
    
    public GenreUpdateDTO() {}

    public GenreUpdateDTO(String name) {
        this.name = name;
    }


    
}
