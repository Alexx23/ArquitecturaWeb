package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.Room;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRoom.RoomValidator.class)
public @interface ValidRoom {
    String message() default "debe ser una sala existente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class RoomValidator implements ConstraintValidator<ValidRoom, Integer> {
        
        @Override
        public void initialize(ValidRoom constraintAnnotation) {
        }

        @Override
        public boolean isValid(Integer roomId, ConstraintValidatorContext context) {
            if (roomId == null) return true;
            Room room = ModelController.getRoom().findRoom(roomId);
            return room != null;
        }
    }
}