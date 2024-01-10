package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.Card;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCard.CardValidator.class)
public @interface ValidCard {
    String message() default "debe ser una tarjeta existente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CardValidator implements ConstraintValidator<ValidCard, Integer> {
        
        @Override
        public void initialize(ValidCard constraintAnnotation) {
        }

        @Override
        public boolean isValid(Integer cardId, ConstraintValidatorContext context) {
            if (cardId == null) return true;
            Card card = ModelController.getCard().findCard(cardId);
            return card != null;
        }
    }
}