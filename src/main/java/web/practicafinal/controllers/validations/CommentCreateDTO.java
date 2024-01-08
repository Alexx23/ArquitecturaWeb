package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.ValidMovie;

/**
 *
 * @author Alex
 */
public class CommentCreateDTO {
    
    @NotNull
    @ValidMovie
    private Integer movieId;
    
    @NotNull
    @Size(min = 1, max = 2000)
    private String content;
    
    public CommentCreateDTO() {}

    public CommentCreateDTO(Integer movieId, String content) {
        this.movieId = movieId;
        this.content = content;
    }
    
}
