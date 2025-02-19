package br.com.ccs.contaspagar.domain.util;

import br.com.ccs.contaspagar.domain.core.exception.ContasPagarException;
import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.vo.Situacao;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContaCsvReaderTest {

    @Test
    public void test_readCsv_emptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("empty.csv", "".getBytes());
        assertThrows(ContasPagarException.class, () -> ContaCsvReader.readCsv(emptyFile));
    }

    @Test
    public void test_readCsv_invalidDateFormat() {
        String content = "01-05-2023,,1000.00,Descrição,PENDENTE\n"; // Incorrect date format
        MultipartFile invalidFile = new MockMultipartFile("invalid.csv", content.getBytes(StandardCharsets.UTF_8));
        assertThrows(ContasPagarException.class, () -> ContaCsvReader.readCsv(invalidFile));
    }

    @Test
    public void test_readCsv_invalidFieldCount() {
        String content = "2023-05-01,1000.00,Descrição,PENDENTE\n"; // Missing dataPagamento field
        MultipartFile invalidFile = new MockMultipartFile("invalid.csv", content.getBytes(StandardCharsets.UTF_8));
        assertThrows(ContasPagarException.class, () -> ContaCsvReader.readCsv(invalidFile));
    }

    @Test
    public void test_readCsv_invalidSituacao() {
        String content = "2023-05-01,,1000.00,Descrição,INVALID_STATUS\n"; // Invalid situacao
        MultipartFile invalidFile = new MockMultipartFile("invalid.csv", content.getBytes(StandardCharsets.UTF_8));
        assertThrows(ContasPagarException.class, () -> ContaCsvReader.readCsv(invalidFile));
    }

    @Test
    public void test_readCsv_invalidValorFormat() {
        String content = "2023-05-01,,abc,Descrição,PENDENTE\n"; // Invalid valor
        MultipartFile invalidFile = new MockMultipartFile("invalid.csv", content.getBytes(StandardCharsets.UTF_8));
        assertThrows(ContasPagarException.class, () -> ContaCsvReader.readCsv(invalidFile));
    }

    @Test
    public void test_readCsv_ioException() throws IOException {
        MultipartFile errorFile = new MockMultipartFile("error.csv", "test".getBytes()) {
            @Override
            public java.io.InputStream getInputStream() throws IOException {
                throw new IOException("Simulated IO error");
            }
        };
        assertThrows(ContasPagarException.class, () -> ContaCsvReader.readCsv(errorFile));
    }

    @Test
    public void test_readCsv_withValidMultipleLines() throws IOException {
        String csvContent = "2023-05-01,2023-05-01,100.50,Conta de luz,PAGA\n" +
                            "2023-06-01,,200.75,Aluguel,PENDENTE";
        MultipartFile file = new MockMultipartFile("test.csv", csvContent.getBytes(StandardCharsets.UTF_8));

        List<Conta> result = ContaCsvReader.readCsv(file);

        assertEquals(2, result.size());

        Conta conta1 = result.getFirst();
        assertEquals(LocalDate.of(2023, 5, 1), conta1.getDataVencimento());
        assertEquals(LocalDate.of(2023, 5, 1), conta1.getDataPagamento());
        assertEquals(new BigDecimal("100.50"), conta1.getValor());
        assertEquals("Conta de luz", conta1.getDescricao());
        assertEquals(Situacao.PAGA, conta1.getSituacao());

        Conta conta2 = result.get(1);
        assertEquals(LocalDate.of(2023, 6, 1), conta2.getDataVencimento());
        assertNull(conta2.getDataPagamento());
        assertEquals(new BigDecimal("200.75"), conta2.getValor());
        assertEquals("Aluguel", conta2.getDescricao());
        assertEquals(Situacao.PENDENTE, conta2.getSituacao());
    }

}