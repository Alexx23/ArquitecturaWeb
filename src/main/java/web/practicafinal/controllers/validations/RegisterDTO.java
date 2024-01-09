package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.UniqueUsername;
import web.practicafinal.controllers.validations.annotations.UniqueEmail;
import web.practicafinal.controllers.validations.annotations.ValidEmail;
import web.practicafinal.controllers.validations.annotations.ValidUsername;

/**
 *
 * @author Alex
 */
public class RegisterDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    
    @NotNull
    @Size(min = 1, max = 255)
    @ValidUsername
    @UniqueUsername
    private String username;
    
    @NotNull
    @Size(min = 1, max = 255)
    @Email
    @ValidEmail
    @UniqueEmail
    private String email;
    
    @NotNull
    @Size(min = 8, max = 255)
    private String password;
    
    @NotNull
    @Size(min = 8, max = 255)
    private String passwordConfirmation;
    
    public RegisterDTO() {}

    public RegisterDTO(String name, String username, String email, String password, String passwordConfirmation) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }

    
    
}
