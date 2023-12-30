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
@Constraint(validatedBy = UniqueUsername.UserValidator.class)
public @interface UniqueUsername {
    String message() default "ya está siendo usado por otro usuario";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class UserValidator implements ConstraintValidator<UniqueUsername, String> {
        
        @Override
        public void initialize(UniqueUsername constraintAnnotation) {
        }

        @Override
        public boolean isValid(String username, ConstraintValidatorContext context) {
            if (username == null) return true;
            User user = UserHelper.getUserByUsername(username);
            return user == null;
        }
    }
}