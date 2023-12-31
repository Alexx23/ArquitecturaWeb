package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.Nationality;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.models.controllers.NationalityJpaController;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidNationality.NationalityValidator.class)
public @interface ValidNationality {
    String message() default "debe ser una nacionalidad existente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class NationalityValidator implements ConstraintValidator<ValidNationality, Integer> {
        
        @Override
        public void initialize(ValidNationality constraintAnnotation) {
        }

        @Override
        public boolean isValid(Integer nationalityId, ConstraintValidatorContext context) {
            if (nationalityId == null) return true;
            Nationality nationality = ModelController.getNationality().findNationality(nationalityId);
            return nationality != null;
        }
    }
}