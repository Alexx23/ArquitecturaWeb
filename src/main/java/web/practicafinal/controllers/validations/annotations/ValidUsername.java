package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUsername.UserValidator.class)
public @interface ValidUsername {
    String message() default "solo puede contener números, letras y barras bajas";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class UserValidator implements ConstraintValidator<ValidUsername, String> {
        
        @Override
        public void initialize(ValidUsername constraintAnnotation) {
        }

        @Override
        public boolean isValid(String username, ConstraintValidatorContext context) {
            if (username == null) return true;
            if (username.split(" ").length != 1) return false;
            if (!username.matches("[0-9a-zA-Z_]+")) return false;
            return true;
        }
    }
}