package br.com.ccs.contaspagar.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ContasPagarAuthenticationException extends ContasPagarException {

    public ContasPagarAuthenticationException(String message) {
        super(message);
    }

    public ContasPagarAuthenticationException(String msg, Throwable e) {
        super(msg, e);
    }
}
