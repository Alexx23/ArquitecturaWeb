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
import web.practicafinal.models.controllers.GenreJpaController;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidGenre.GenreValidator.class)
public @interface ValidGenre {
    String message() default "debe ser un género existente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class GenreValidator implements ConstraintValidator<ValidGenre, Integer> {
        
        @Override
        public void initialize(ValidGenre constraintAnnotation) {
        }

        @Override
        public boolean isValid(Integer genreId, ConstraintValidatorContext context) {
            if (genreId == null) return true;
            Genre genre = ModelController.getGenre().findGenre(genreId);
            return genre != null;
        }
    }
}