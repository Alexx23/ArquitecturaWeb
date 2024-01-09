package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 *
 * @author Alex
 */
public class CardCreateDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    private String title;
    
    @NotNull
    @Min(value = 0)
    private Long cardNumber;
    
    @NotNull
    @FutureOrPresent
    private Date expiration;
    
    @NotNull
    @Min(value = 0)
    private Integer cvv;
    
    public CardCreateDTO() {}

    public CardCreateDTO(String title, Long cardNumber, Date expiration, Integer cvv) {
        this.title = title;
        this.cardNumber = cardNumber;
        this.expiration = expiration;
        this.cvv = cvv;

    }

    
    
}
