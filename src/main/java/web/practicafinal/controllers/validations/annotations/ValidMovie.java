package web.practicafinal.controllers.validations.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import web.practicafinal.models.Movie;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidMovie.MovieValidator.class)
public @interface ValidMovie {
    String message() default "debe ser una película existente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class MovieValidator implements ConstraintValidator<ValidMovie, Integer> {
        
        @Override
        public void initialize(ValidMovie constraintAnnotation) {
        }

        @Override
        public boolean isValid(Integer movieId, ConstraintValidatorContext context) {
            if (movieId == null) return true;
            Movie movie = ModelController.getMovie().findMovie(movieId);
            return movie != null;
        }
    }
}