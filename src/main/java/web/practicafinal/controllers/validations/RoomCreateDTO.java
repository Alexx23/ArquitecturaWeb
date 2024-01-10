package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.UniqueRoomName;

/**
 *
 * @author Alex
 */
public class RoomCreateDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    @UniqueRoomName
    private String name;

    @NotNull
    @Min(value = 1)
    private Short depth;
    
    @NotNull
    @Min(value = 1)
    private Short seats;
    
    public RoomCreateDTO() {}

    public RoomCreateDTO(String name, Short depth, Short seats) {
        this.name = name;
        this.depth = depth;
        this.seats = seats;
    }


    
}
