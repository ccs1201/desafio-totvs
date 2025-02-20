package br.com.ccs.contaspagar.domain.util;

import br.com.ccs.contaspagar.api.v1.model.input.CsvInput;
import br.com.ccs.contaspagar.infra.exception.CsvReaderException;
import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.vo.Situacao;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

@UtilityClass
public class ContaCsvReader {

    public List<Conta> readCsv(CsvInput csvInput) {

        if (isNull(csvInput) || isNull(csvInput.multipartFile()) || csvInput.multipartFile().isEmpty()) {
            throw new CsvReaderException("O arquivo está vazio.");
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvInput.multipartFile().getInputStream(), StandardCharsets.UTF_8))) {

            AtomicInteger lineNumber = new AtomicInteger(0);

            return reader.lines()
                    .map(line -> {
                        lineNumber.incrementAndGet();
                        return parseLine(line, lineNumber.get());
                    })
                    .toList();
        } catch (Exception e) {
            throw new CsvReaderException("Erro ao processar o arquivo CSV: " + e.getMessage(), e);
        }
    }

    private Conta parseLine(String line, int lineNumber) {
        try {
            String[] fields = line.split(",");

            if (fields.length != 5) {
                throw new CsvReaderException(
                        String.format("Linha %d: número incorreto de campos. Esperado: 5, Encontrado: %d",
                                lineNumber, fields.length));
            }

            return Conta.builder()
                    .dataVencimento(parseDate(fields[0], lineNumber, "Data de Vencimento"))
                    .dataPagamento(parseOptionalDate(fields[1], lineNumber))
                    .valor(parseValor(fields[2], lineNumber))
                    .descricao(fields[3].trim())
                    .situacao(parseSituacao(fields[4], lineNumber))
                    .importadaViaCsv(true)
                    .build();

        } catch (Exception e) {
            throw new CsvReaderException(
                    String.format("Erro ao processar linha %d: %s", lineNumber, e.getMessage()), e);
        }
    }

    private LocalDate parseDate(String date, int lineNumber, String fieldName) {
        try {
            return LocalDate.parse(date.trim());
        } catch (DateTimeParseException e) {
            throw new CsvReaderException(
                    String.format("Linha %d: formato de data inválido para %s: %s",
                            lineNumber, fieldName, date));
        }
    }

    private LocalDate parseOptionalDate(String date, int lineNumber) {
        return date.trim().isEmpty() ? null : parseDate(date, lineNumber, "Data de Pagamento");
    }

    private BigDecimal parseValor(String valor, int lineNumber) {
        try {
            return new BigDecimal(valor.trim());
        } catch (NumberFormatException e) {
            throw new CsvReaderException(
                    String.format("Linha %d: valor inválido: %s", lineNumber, valor));
        }
    }

    private Situacao parseSituacao(String situacao, int lineNumber) {
        try {
            return Situacao.valueOf(situacao.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CsvReaderException(
                    String.format("Linha %d: situação inválida: %s", lineNumber, situacao));
        }
    }
}

