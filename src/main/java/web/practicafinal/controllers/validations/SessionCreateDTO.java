package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import web.practicafinal.controllers.validations.annotations.ValidMovie;
import web.practicafinal.controllers.validations.annotations.ValidRoom;

/**
 *
 * @author Alex
 */
public class SessionCreateDTO {
    
    @NotNull
    @Min(value = 1)
    @ValidMovie
    private Integer movieId;

    @NotNull
    @Min(value = 1)
    @ValidRoom
    private Integer roomId;
    
    @NotNull
    @FutureOrPresent
    private Date datetime;
    
    public SessionCreateDTO() {}

    public SessionCreateDTO(Integer movieId, Integer roomId, Date datetime) {
        this.movieId = movieId;
        this.roomId = roomId;
        this.datetime = datetime;
    }


    
}
