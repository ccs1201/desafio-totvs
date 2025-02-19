package br.com.ccs.contaspagar.domain.core.exception;

public class ContasPagarServiceException extends ContasPagarException {
    public ContasPagarServiceException(String msg) {
        super(msg);

    }

    public ContasPagarServiceException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
