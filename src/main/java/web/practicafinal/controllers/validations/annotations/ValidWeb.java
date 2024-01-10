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
@Constraint(validatedBy = ValidWeb.MovieValidator.class)
public @interface ValidWeb {
    String message() default "debe comenzar por 'http://' o 'https://' ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class MovieValidator implements ConstraintValidator<ValidWeb, String> {
        
        @Override
        public void initialize(ValidWeb constraintAnnotation) {
        }

        @Override
        public boolean isValid(String web, ConstraintValidatorContext context) {
            if (web == null || web.equalsIgnoreCase("")) return true;
            if (!web.toLowerCase().startsWith("http://") && !web.toLowerCase().startsWith("https://")) return false;
            return true;
        }
    }
}