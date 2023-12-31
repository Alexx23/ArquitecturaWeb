package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.Director;
import web.practicafinal.models.controllers.DirectorJpaController;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDirector.DirectorValidator.class)
public @interface ValidDirector {
    String message() default "debe ser un director existente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class DirectorValidator implements ConstraintValidator<ValidDirector, Integer> {
        
        @Override
        public void initialize(ValidDirector constraintAnnotation) {
        }

        @Override
        public boolean isValid(Integer directorId, ConstraintValidatorContext context) {
            if (directorId == null) return true;
            Director director = ModelController.getDirector().findDirector(directorId);
            return director != null;
        }
    }
}