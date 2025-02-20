package br.com.ccs.contaspagar.domain.util;

import br.com.ccs.contaspagar.api.v1.model.input.CsvInput;
import br.com.ccs.contaspagar.infra.exception.CsvReaderException;
import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.vo.SituacaoEnum;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContaCsvReaderTest {

    @Test
    void test_readCsv_emptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("empty.csv", "".getBytes());
        var input = new CsvInput(emptyFile);
        assertThrows(CsvReaderException.class, () -> ContaCsvReader.readCsv(input));
    }

    @Test
    void test_readCsv_invalidDateFormat() {
        String content = "01-05-2023,,1000.00,Descrição,PENDENTE\n"; // Incorrect date format
        MultipartFile invalidFile = new MockMultipartFile("invalid.csv", content.getBytes(StandardCharsets.UTF_8));
        var input = new CsvInput(invalidFile);
        assertThrows(CsvReaderException.class, () -> ContaCsvReader.readCsv(input));
    }

    @Test
    void test_readCsv_invalidFieldCount() {
        String content = "2023-05-01,1000.00,Descrição,PENDENTE\n"; // Missing dataPagamento field
        MultipartFile invalidFile = new MockMultipartFile("invalid.csv", content.getBytes(StandardCharsets.UTF_8));
        var input = new CsvInput(invalidFile);
        assertThrows(CsvReaderException.class, () -> ContaCsvReader.readCsv(input));
    }

    @Test
    void test_readCsv_invalidSituacao() {
        String content = "2023-05-01,,1000.00,Descrição,INVALID_STATUS\n"; // Invalid situacao
        MultipartFile invalidFile = new MockMultipartFile("invalid.csv", content.getBytes(StandardCharsets.UTF_8));
        var input = new CsvInput(invalidFile);
        assertThrows(CsvReaderException.class, () -> ContaCsvReader.readCsv(input));
    }

    @Test
    void test_readCsv_invalidValorFormat() {
        String content = "2023-05-01,,abc,Descrição,PENDENTE\n"; // Invalid valor
        MultipartFile invalidFile = new MockMultipartFile("invalid.csv", content.getBytes(StandardCharsets.UTF_8));
        var input = new CsvInput(invalidFile);
        assertThrows(CsvReaderException.class, () -> ContaCsvReader.readCsv(input));
    }

    @Test
    void test_readCsv_ioException() {
        MultipartFile errorFile = new MockMultipartFile("error.csv", "test".getBytes()) {
            @Override
            public java.io.InputStream getInputStream() throws IOException {
                throw new IOException("Simulando IO error");
            }
        };

        var input = new CsvInput(errorFile);
        assertThrows(CsvReaderException.class, () -> ContaCsvReader.readCsv(input));
    }

    @Test
    void test_readCsv_withValidMultipleLines() {
        String csvContent = "2023-05-01,2023-05-01,100.50,Conta de luz,PAGA\n" +
                "2023-06-01,,200.75,Aluguel,PENDENTE";
        MultipartFile file = new MockMultipartFile("test.csv", csvContent.getBytes(StandardCharsets.UTF_8));
        var input = new CsvInput(file);

        List<Conta> result = ContaCsvReader.readCsv(input);

        assertEquals(2, result.size());

        Conta conta1 = result.getFirst();
        assertEquals(LocalDate.of(2023, 5, 1), conta1.getDataVencimento());
        assertEquals(LocalDate.of(2023, 5, 1), conta1.getDataPagamento());
        assertEquals(new BigDecimal("100.50"), conta1.getValor());
        assertEquals("Conta de luz", conta1.getDescricao());
        assertEquals(SituacaoEnum.PAGA, conta1.getSituacao());

        Conta conta2 = result.get(1);
        assertEquals(LocalDate.of(2023, 6, 1), conta2.getDataVencimento());
        assertNull(conta2.getDataPagamento());
        assertEquals(new BigDecimal("200.75"), conta2.getValor());
        assertEquals("Aluguel", conta2.getDescricao());
        assertEquals(SituacaoEnum.PENDENTE, conta2.getSituacao());
    }

    @Test
    void testQuandoCsvInputIsNull() {
        assertThrows(CsvReaderException.class, () -> ContaCsvReader.readCsv(null));
    }

    @Test
    void testQuandoCsvInputNaoTemArquivo() {
        var input = new CsvInput(null);
        assertThrows(CsvReaderException.class, () -> ContaCsvReader.readCsv(input));
    }

}