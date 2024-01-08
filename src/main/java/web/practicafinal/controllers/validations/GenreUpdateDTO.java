package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Size;

/**
 *
 * @author Alex
 */
public class GenreUpdateDTO {
    
    @Size(min = 1, max = 255)
    private String name;
    
    public GenreUpdateDTO() {}

    public GenreUpdateDTO(String name) {
        this.name = name;
    }


    
}
