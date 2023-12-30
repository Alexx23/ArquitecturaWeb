package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Min;
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
public class MovieUpdateDTO {
    
    @Size(min = 1, max = 255)
    private String name;
    
    @Size(min = 1, max = 255)
    private String web;
    
    @Size(min = 1, max = 255)
    private String originalTitle;
    
    @Min(value = 0)
    private Short duration;
    
    @Min(value = 0)
    private Short year;
    
    @Min(value = 1)
    @ValidGenre
    private Integer genreId;
    
    @Min(value = 1)
    @ValidNationality
    private Integer nationalityId;
    
    @Min(value = 1)
    @ValidDistributor
    private Integer distributorId;
    
    @Min(value = 1)
    @ValidDirector
    private Integer directorId;
    
    @Min(value = 1)
    @ValidAgeClassification
    private Integer ageClassificationId;
    
    public MovieUpdateDTO() {}

    public MovieUpdateDTO(String name, String web, String originalTitle, Short duration, Short year, Integer genreId, Integer nationalityId, Integer distributorId, Integer directorId, Integer ageClassificationId) {
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
