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
import web.practicafinal.models.helpers.NationalityHelper;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNationalityName.NationalityValidator.class)
public @interface UniqueNationalityName {
    String message() default "ya está siendo usado por otra nacionalidad";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class NationalityValidator implements ConstraintValidator<UniqueNationalityName, String> {
        
        @Override
        public void initialize(UniqueNationalityName constraintAnnotation) {
        }

        @Override
        public boolean isValid(String name, ConstraintValidatorContext context) {
            if (name == null) return true;
            Nationality nationality = NationalityHelper.getNationalityByName(name);
            return nationality == null;
        }
    }
}