package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.UniqueRoomName;

/**
 *
 * @author Alex
 */
public class RoomUpdateDTO {
    
    @Size(min = 1, max = 255)
    @UniqueRoomName
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
