package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.ValidAgeClassification;
import web.practicafinal.controllers.validations.annotations.ValidDirector;
import web.practicafinal.controllers.validations.annotations.ValidDistributor;
import web.practicafinal.controllers.validations.annotations.ValidGenre;
import web.practicafinal.controllers.validations.annotations.ValidNationality;

/**
 *
 * @author Alex
 */
public class MovieCreateDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    
    @NotNull
    @Size(min = 1, max = 255)
    private String web;
    
    @NotNull
    @Size(min = 1, max = 255)
    private String originalTitle;
    
    @NotNull
    @Min(value = 1)
    private Short duration;
    
    @NotNull
    @Min(value = 1)
    private Short year;
    
    @NotNull
    @Min(value = 1)
    @ValidGenre
    private Integer genreId;
    
    @NotNull
    @Min(value = 1)
    @ValidNationality
    private Integer nationalityId;
    
    @NotNull
    @Min(value = 1)
    @ValidDistributor
    private Integer distributorId;
    
    @NotNull
    @Min(value = 1)
    @ValidDirector
    private Integer directorId;
    
    @NotNull
    @Min(value = 1)
    @ValidAgeClassification
    private Integer ageClassificationId;
    
    public MovieCreateDTO() {}

    public MovieCreateDTO(String name, String web, String originalTitle, Short duration, Short year, Integer genreId, Integer nationalityId, Integer distributorId, Integer directorId, Integer ageClassificationId) {
        this.name = name;
        this.web = web;
        this.originalTitle = originalTitle;
        this.duration = duration;
        this.year = year;
        this.genreId = genreId;
        this.nationalityId = nationalityId;
        this.distributorId = distributorId;
        this.directorId = directorId;
        this.ageClassificationId = ageClassificationId;
    }


    
}
