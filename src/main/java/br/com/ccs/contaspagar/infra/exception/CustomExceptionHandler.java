package br.com.ccs.contaspagar.infra.exception;

import br.com.ccs.contaspagar.infra.exception.model.ProblemaDetailResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler {

    private final HttpServletRequest request;

    public CustomExceptionHandler(HttpServletRequest request) {
        this.request = request;
    }

    @ExceptionHandler(ContasPagarException.class)
    public ResponseEntity<ProblemaDetailResponse> handleContasPagarServiceException(ContasPagarException ex) {
        var status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.BAD_REQUEST;
        var msg = ex.getMessage() != null ? ex.getMessage() : "Erro ao processar requisição.";

        if (ex.getCause() instanceof DataIntegrityViolationException) {
            var root = ExceptionUtils.getRootCause(ex.getCause());
            msg = msg.concat(" - ").concat(root.getMessage().substring(root.getMessage().indexOf("Detail")));
        }

        return ResponseEntity.status(status).body(ProblemaDetailResponse.builder()
                .status(status.value())
                .title(status.getReasonPhrase())
                .detail(msg)
                .path(request.getRequestURI())
                .build());

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemaDetailResponse handleException(MethodArgumentNotValidException e) {
        return ProblemaDetailResponse
                .builder()
                .title("Erro de validação")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(e.getBindingResult().getFieldErrors().stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .collect(Collectors.joining(", ")))
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemaDetailResponse handleException(Exception e) {
        return ProblemaDetailResponse
                .builder()
                .title("Erro interno")
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .detail(e.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}
