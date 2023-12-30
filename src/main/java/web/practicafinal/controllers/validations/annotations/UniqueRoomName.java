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
import web.practicafinal.models.helpers.RoomHelper;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueRoomName.RoomValidator.class)
public @interface UniqueRoomName {
    String message() default "ya está siendo usado por otra sala";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class RoomValidator implements ConstraintValidator<UniqueRoomName, String> {
        
        @Override
        public void initialize(UniqueRoomName constraintAnnotation) {
        }

        @Override
        public boolean isValid(String name, ConstraintValidatorContext context) {
            if (name == null) return true;
            Room room = RoomHelper.getRoomByName(name);
            return room == null;
        }
    }
}