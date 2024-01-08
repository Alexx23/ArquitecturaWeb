package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 *
 * @author Alex
 */
public class RoomUpdateDTO {
    
    @Size(min = 1, max = 255)
    private String name;

    @Min(value = 1)
    private Short files;
    
    @Min(value = 1)
    private Short cols;
    
    public RoomUpdateDTO() {}

    public RoomUpdateDTO(String name, Short files, Short cols) {
        this.name = name;
        this.files = files;
        this.cols = cols;
    }


    
}
