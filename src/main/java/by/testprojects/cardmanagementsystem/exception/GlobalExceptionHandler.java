package by.testprojects.cardmanagementsystem.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

import java.net.URI;
import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler implements ProblemHandling, SecurityAdviceTrait {

    // Автоматически обрабатывает:
    // - ConstraintViolationException (валидация @Valid)
    // - Все AbstractThrowableProblem
    // - Spring Security исключения

    // Пример кастомной обработки других исключений:
    /*
    @ExceptionHandler(SomeException.class)
    public ResponseEntity<Problem> handleSomeException(SomeException ex) {
        Problem problem = Problem.builder()
            .withType(URI.create("..."))
            .withStatus(Status.BAD_REQUEST)
            .withTitle("...")
            .build();
        return create(ex, problem);
    }
    */

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Problem> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Problem problem = Problem.builder()
                .withTitle("Violation of an integrity constraint")
                .withStatus(Status.CONFLICT)
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Problem> handleExpiredJwt(NullPointerException ex) {
        Problem problem = Problem.builder()
                .withType(URI.create("https://example.com/errors/jwt-expired"))
                .withTitle("Token Expired")
                .withStatus(Status.UNAUTHORIZED)
                .withDetail("JWT expired: " + ex.getMessage())
                .with("timestamp", Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

}
