package br.com.ccs.contaspagar.infra.exception;

import org.springframework.http.HttpStatus;

public class ContasPagarAuthenticationException extends ContasPagarException {

    public ContasPagarAuthenticationException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
