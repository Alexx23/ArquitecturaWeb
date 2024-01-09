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
@Constraint(validatedBy = ValidEmail.UserValidator.class)
public @interface ValidEmail {
    String message() default "solo puede contener números, letras y barras bajas";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class UserValidator implements ConstraintValidator<ValidEmail, String> {
        
        @Override
        public void initialize(ValidEmail constraintAnnotation) {
        }

        @Override
        public boolean isValid(String email, ConstraintValidatorContext context) {
            if (email == null) return true;
            if (email.split(" ").length != 1) return false;
            return true;
        }
    }
}