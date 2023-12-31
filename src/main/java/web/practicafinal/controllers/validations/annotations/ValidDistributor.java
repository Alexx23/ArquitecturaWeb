package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.Distributor;
import web.practicafinal.models.controllers.DistributorJpaController;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDistributor.DistributorValidator.class)
public @interface ValidDistributor {
    String message() default "debe ser un distribuidor existente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class DistributorValidator implements ConstraintValidator<ValidDistributor, Integer> {
        
        @Override
        public void initialize(ValidDistributor constraintAnnotation) {
        }

        @Override
        public boolean isValid(Integer distributorId, ConstraintValidatorContext context) {
            if (distributorId == null) return true;
            Distributor distributor = ModelController.getDistributor().findDistributor(distributorId);
            return distributor != null;
        }
    }
}