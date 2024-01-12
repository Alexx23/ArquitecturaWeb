package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.NotNull;
import web.practicafinal.controllers.validations.annotations.ValidMovie;

/**
 *
 * @author Alex
 */
public class FavoriteDTO {
    
    @NotNull
    @ValidMovie
    private Integer movieId;
    
    public FavoriteDTO() {}

    public FavoriteDTO(Integer movieId) {
        this.movieId = movieId;
    }
    
}
