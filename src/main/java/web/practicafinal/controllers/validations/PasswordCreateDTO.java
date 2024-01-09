package web.practicafinal.controllers.validations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author Alex
 */
public class PasswordCreateDTO {
    
    @NotNull
    @Size(min = 1, max = 255)
    private String currentPassword;
    
    @NotNull
    @Size(min = 8, max = 255)
    private String newPassword;
    
    @NotNull
    @Size(min = 8, max = 255)
    private String newPasswordConfirmation;
    
    public PasswordCreateDTO() {}

    public PasswordCreateDTO(String currentPassword, String newPassword, String newPasswordConfirmation) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;

    }

    
    
}
