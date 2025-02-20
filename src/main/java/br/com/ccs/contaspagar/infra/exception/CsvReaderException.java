package br.com.ccs.contaspagar.infra.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class CsvReaderException extends ContasPagarException {

    public CsvReaderException(String msg, Throwable e) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, msg, e);
        log.error(msg, e);
    }

    public CsvReaderException(String msg) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, msg);
    }
}
