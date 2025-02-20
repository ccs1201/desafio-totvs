package br.com.ccs.contaspagar.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ContasPagarServiceException extends ContasPagarException {
    public ContasPagarServiceException(String msg) {
        super(msg);

    }

    public ContasPagarServiceException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
