package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.Session;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSession.SessionValidator.class)
public @interface ValidSession {
    String message() default "debe ser una session existente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class SessionValidator implements ConstraintValidator<ValidSession, Integer> {
        
        @Override
        public void initialize(ValidSession constraintAnnotation) {
        }

        @Override
        public boolean isValid(Integer sessionId, ConstraintValidatorContext context) {
            if (sessionId == null) return true;
            Session session = ModelController.getSession().findSession(sessionId);
            return session != null;
        }
    }
}