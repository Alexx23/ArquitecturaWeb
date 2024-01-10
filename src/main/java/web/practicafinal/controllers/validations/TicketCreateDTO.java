package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import web.practicafinal.controllers.validations.annotations.ValidCard;
import web.practicafinal.controllers.validations.annotations.ValidSession;

/**
 *
 * @author Alex
 */
public class TicketCreateDTO {
    
    @NotNull
    @Min(value = 1)
    @ValidCard
    private Integer card_id;

    @NotNull
    @Min(value = 1)
    @ValidSession
    private Integer session_id;
    
    public TicketCreateDTO() {}

    public TicketCreateDTO(Integer card_id, Integer session_id) {
        this.card_id = card_id;
        this.session_id = session_id;
    }


    
}
