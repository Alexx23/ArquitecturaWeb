package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author Alex
 */
public class RequestPasswordDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    @Email
    private String email;
    
    
    public RequestPasswordDTO() {}

    public RequestPasswordDTO(String email) {
        this.email = email;

    }

    
    
}
