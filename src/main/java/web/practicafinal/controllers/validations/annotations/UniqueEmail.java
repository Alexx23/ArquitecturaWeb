package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.User;
import web.practicafinal.models.helpers.UserHelper;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmail.UserValidator.class)
public @interface UniqueEmail {
    String message() default "ya está siendo usado por otro usuario";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class UserValidator implements ConstraintValidator<UniqueEmail, String> {
        
        @Override
        public void initialize(UniqueEmail constraintAnnotation) {
        }

        @Override
        public boolean isValid(String email, ConstraintValidatorContext context) {
            if (email == null) return true;
            User user = UserHelper.getUserByEmail(email);
            return user == null;
        }
    }
}