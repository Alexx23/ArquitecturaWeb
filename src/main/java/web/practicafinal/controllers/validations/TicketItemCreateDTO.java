package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Alex
 */
public class TicketItemCreateDTO {
    
    @NotNull
    @Min(value = 0)
    private Short depth;

    @NotNull
    @Min(value = 0)
    private Short seat;
    
    public TicketItemCreateDTO() {}

    public TicketItemCreateDTO(Short depth, Short seat) {
        this.depth = depth;
        this.seat = seat;
    }

    public Short getDepth() {
        return depth;
    }

    public Short getSeat() {
        return seat;
    }


    
}
