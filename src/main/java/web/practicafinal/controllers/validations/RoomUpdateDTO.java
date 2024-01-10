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
    private Short depth;
    
    @Min(value = 1)
    private Short seats;
    
    public RoomUpdateDTO() {}

    public RoomUpdateDTO(String name, Short depth, Short seats) {
        this.name = name;
        this.depth = depth;
        this.seats = seats;
    }


    
}
