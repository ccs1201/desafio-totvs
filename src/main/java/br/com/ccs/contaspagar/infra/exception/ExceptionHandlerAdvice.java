package br.com.ccs.contaspagar.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(MethodArgumentNotValidException e) {
        return ErrorResponse
                .builder(e,
                        ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                e.getBindingResult().getFieldErrors().stream()
                                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                        .collect(Collectors.joining(", "))))
                .build();
    }

    @ExceptionHandler(ContasPagarException.class)
    public ResponseEntity<ErrorResponse> handleContasPagarServiceException(ContasPagarServiceException ex) {
        var error = new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
