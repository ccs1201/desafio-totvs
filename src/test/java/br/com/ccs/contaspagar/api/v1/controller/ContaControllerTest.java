package br.com.ccs.contaspagar.api.v1.controller;

import br.com.ccs.contaspagar.api.v1.model.input.ContaInput;
import br.com.ccs.contaspagar.api.v1.model.input.CsvInput;
import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.service.ContaService;
import br.com.ccs.contaspagar.domain.vo.Situacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {

    @InjectMocks
    private ContaController contaController;

    @Mock
    private ContaService contaService;

    private static ContaInput getContaInput() {
        return new
                ContaInput(LocalDate.now().plusDays(1), null, BigDecimal.TEN, "teste", Situacao.PENDENTE);
    }

    private static Conta getConta() {
        return Conta.builder()
                .dataVencimento(LocalDate.now().plusDays(1))
                .dataPagamento(LocalDate.now().plusDays(1))
                .valor(BigDecimal.TEN)
                .descricao("teste")
                .situacao(Situacao.PENDENTE)
                .build();
    }

    private static Page<Conta> getPage() {
        return new PageImpl<>(List.of(getConta()), Pageable.ofSize(5), 10);
    }

    @Test
    void criarConta() {
        when(contaService.salvar(any(Conta.class))).thenReturn(getConta());
        assertDoesNotThrow(() -> contaController.criarConta(getContaInput()));
        verify(contaService, times(1)).salvar(any(Conta.class));
    }

    @Test
    void listarContas() {
        when(contaService.listarTodas(any())).thenReturn(getPage());
        assertDoesNotThrow(() -> contaController.listarContas(0, 5, "dataVencimento", Sort.Direction.ASC));
        verify(contaService, times(1)).listarTodas(Mockito.any());
    }

    @Test
    void buscarConta() {
        when(contaService.buscarPorId(any())).thenReturn(getConta());
        assertDoesNotThrow(() -> contaController.buscarConta(getConta().getId()));
        verify(contaService, times(1)).buscarPorId(any());
    }

    @Test
    void atualizarConta() {
        when(contaService.salvar(any(Conta.class))).thenReturn(getConta());
        assertDoesNotThrow(() -> contaController.atualizarConta(getConta().getId(), getContaInput()));
        verify(contaService, times(1)).salvar(any(Conta.class));
    }

    @Test
    void pagarConta() {
        doNothing().when(contaService).pagar(any(), any());
        assertDoesNotThrow(() -> contaController.pagarConta(getConta().getId(), LocalDate.now().plusDays(1)));
        verify(contaService, times(1)).pagar(any(), any());
    }

    @Test
    void cancelarConta() {
        doNothing().when(contaService).cancelar(any());
        assertDoesNotThrow(() -> contaController.cancelarConta(getConta().getId()));
        verify(contaService, times(1)).cancelar(any());
    }

    @Test
    void filtro() {
        when(contaService.findByVencimentoEDescricao(any(), any(), any())).thenReturn(getPage());
        assertDoesNotThrow(() -> contaController.filtro(0, 5, "dataVencimento", Sort.Direction.ASC, LocalDate.now().plusDays(1), "teste"));
        verify(contaService, times(1)).findByVencimentoEDescricao(any(), any(), any());
    }

    @Test
    void obterValorTotalPago() {
        when(contaService.totalPago(any(), any())).thenReturn(BigDecimal.TEN);
        assertDoesNotThrow(() -> contaController.obterValorTotalPago(LocalDate.now().plusDays(1), LocalDate.now().plusDays(1)));
        verify(contaService, times(1)).totalPago(any(), any());
    }

    @Test
    void importarContas() {
        String csvContent = "2023-05-01,2023-05-01,100.50,Conta de luz,PAGA\n" +
                "2023-06-01,,200.75,Aluguel,PENDENTE";
        MultipartFile file = new MockMultipartFile("test.csv", csvContent.getBytes(StandardCharsets.UTF_8));
        var input = new CsvInput(file);

        when(contaService.importarContasCsv(any())).thenReturn(List.of(getConta()));
        var expected = assertDoesNotThrow(() -> contaController.importarContas(input));

        CompletableFuture.allOf(expected).join();

        verify(contaService, times(1)).importarContasCsv(input);
    }
}
