package io.github.adit12t.artifactorymanager.api;

import io.github.adit12t.artifactorymanager.client.ArtifactoryException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ArtifactoryException.class)
    ProblemDetail artifactory(ArtifactoryException e) {
        return ProblemDetail.forStatusAndDetail(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class,
            ConstraintViolationException.class})
    ProblemDetail invalid(Exception e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request parameters.");
    }
}
