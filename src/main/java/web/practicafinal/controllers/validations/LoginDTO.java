package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author Alex
 */
public class LoginDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    private String email;
    
    @NotNull
    @Size(min = 1, max = 255)
    private String password;
    
    public LoginDTO() {}

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
}
