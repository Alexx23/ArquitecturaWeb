package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.AgeClassification;
import web.practicafinal.models.controllers.AgeClassificationJpaController;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAgeClassification.AgeClassificationValidator.class)
public @interface ValidAgeClassification {
    String message() default "debe ser una clasificación de edad existente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class AgeClassificationValidator implements ConstraintValidator<ValidAgeClassification, Integer> {
        @Override
        public void initialize(ValidAgeClassification constraintAnnotation) {
        }

        @Override
        public boolean isValid(Integer ageClassificationId, ConstraintValidatorContext context) {
            if (ageClassificationId == null) return true;
            AgeClassification ageClassification = ModelController.getAgeClassification().findAgeClassification(ageClassificationId);
            return ageClassification != null;
        }
    }
}