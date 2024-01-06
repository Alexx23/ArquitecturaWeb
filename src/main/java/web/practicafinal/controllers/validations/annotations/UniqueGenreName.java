package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.Genre;
import web.practicafinal.models.helpers.GenreHelper;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueGenreName.GenreValidator.class)
public @interface UniqueGenreName {
    String message() default "ya está siendo usado por otro género";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class GenreValidator implements ConstraintValidator<UniqueGenreName, String> {
        
        @Override
        public void initialize(UniqueGenreName constraintAnnotation) {
        }

        @Override
        public boolean isValid(String name, ConstraintValidatorContext context) {
            if (name == null) return true;
            Genre genre = GenreHelper.getGenreByName(name);
            return genre == null;
        }
    }
}