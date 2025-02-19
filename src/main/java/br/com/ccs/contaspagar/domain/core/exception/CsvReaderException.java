package br.com.ccs.contaspagar.domain.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@Slf4j
public class CsvReaderException extends ContasPagarException {

    public CsvReaderException(String msg, Throwable e) {
        super(msg, e);
        log.error(msg, e);
    }

    public CsvReaderException(String msg) {
        super(msg);
    }
}
