package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import web.practicafinal.controllers.validations.annotations.ValidEmail;
import web.practicafinal.controllers.validations.annotations.ValidUsername;

/**
 *
 * @author Alex
 */
public class UserUpdateDTO {
    
    @Size(min = 1, max = 255)
    private String name;
    
    @Size(min = 1, max = 255)
    @ValidUsername
    private String username;
    
    @Size(min = 1, max = 255)
    @Email
    @ValidEmail
    private String email;
    
    public UserUpdateDTO() {}

    public UserUpdateDTO(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;
    }

    
    
}
