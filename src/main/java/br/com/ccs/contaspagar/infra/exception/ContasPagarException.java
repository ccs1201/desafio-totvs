package br.com.ccs.contaspagar.infra.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ContasPagarException extends RuntimeException {
    private final HttpStatus status;

    public ContasPagarException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ContasPagarException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

}
