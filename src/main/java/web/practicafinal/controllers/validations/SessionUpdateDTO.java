package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import java.util.Date;
import web.practicafinal.controllers.validations.annotations.ValidMovie;
import web.practicafinal.controllers.validations.annotations.ValidRoom;

/**
 *
 * @author Alex
 */
public class SessionUpdateDTO {
    
    @Min(value = 1)
    @ValidMovie
    private Integer movieId;

    @Min(value = 1)
    @ValidRoom
    private Integer roomId;
    
    @FutureOrPresent
    private Date datetime;
    
    public SessionUpdateDTO() {}

    public SessionUpdateDTO(Integer movieId, Integer roomId, Date datetime) {
        this.movieId = movieId;
        this.roomId = roomId;
        this.datetime = datetime;
    }


    
}
