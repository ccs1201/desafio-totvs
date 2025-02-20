package br.com.ccs.contaspagar.infra.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class ContasPagarException extends RuntimeException {

    public ContasPagarException(String message) {
        super(message);
    }

    public ContasPagarException(String message, Throwable cause) {
        super(message, cause);
    }

}
