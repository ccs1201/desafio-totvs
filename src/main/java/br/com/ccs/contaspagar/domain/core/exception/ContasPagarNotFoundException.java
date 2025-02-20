package br.com.ccs.contaspagar.domain.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContasPagarNotFoundException extends ContasPagarException {
    public ContasPagarNotFoundException(String s) {
        super(s);
    }
}
