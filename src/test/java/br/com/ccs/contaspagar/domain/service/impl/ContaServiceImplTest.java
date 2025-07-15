package br.com.ccs.contaspagar.domain.service.impl;

import br.com.ccs.contaspagar.api.v1.model.input.CsvInput;
import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.repository.ContaRepository;
import br.com.ccs.contaspagar.domain.vo.SituacaoEnum;
import br.com.ccs.contaspagar.infra.exception.ContasPagarException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceImplTest {

    @InjectMocks
    private ContaServiceImpl contaService;
    @Mock
    private ContaRepository contaRepository;

    @Test
    void testBuscarPorIdReturnsContaWhenFound() {
        UUID id = UUID.randomUUID();
        Conta expectedConta = new Conta();
        when(contaRepository.findById(id)).thenReturn(Optional.of(expectedConta));

        var actual = contaService.buscarPorId(id);

        assertEquals(expectedConta, actual);
        verify(contaRepository, times(1)).findById(id);
    }

    @Test
    void testCancelarContaPaga() {
        UUID id = UUID.randomUUID();
        Conta conta = new Conta();
        conta.setSituacao(SituacaoEnum.PAGA);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        assertThrows(ContasPagarException.class, () -> contaService.cancelar(id));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void testCancelarContaNaoExistente() {
        UUID id = UUID.randomUUID();
        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ContasPagarException.class, () -> contaService.cancelar(id));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void testCancelarSaveFailure() {
        UUID id = UUID.randomUUID();
        Conta conta = new Conta();
        conta.setSituacao(SituacaoEnum.PENDENTE);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenThrow(new RuntimeException("Save failed"));

        assertThrows(ContasPagarException.class, () -> contaService.cancelar(id));
        verify(contaRepository, times(1)).findById(id);
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testCancelarWithNullId() {
        assertThrows(ContasPagarException.class, () -> contaService.cancelar(null));
        verify(contaRepository, never()).findById(any(UUID.class));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void testListarTodasWhenNoAccountsFound() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(contaRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(new ArrayList<>()));

        var exception = assertThrows(ContasPagarException.class,
                () -> contaService.listarTodas(pageRequest));

        assertEquals("Nenhuma conta encontrada.", exception.getMessage());
        verify(contaRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testPagarWhenAccountAlreadyPaid() {
        var id = UUID.randomUUID();
        LocalDate dataPagamento = LocalDate.now();
        var conta = new Conta();
        conta.setSituacao(SituacaoEnum.PAGA);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        assertThrows(ContasPagarException.class, () -> contaService.pagar(id, dataPagamento));
        verify(contaRepository).findById(id);
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void testPagarWhenAccountIsNotPaid() {
        var id = UUID.randomUUID();
        var dataPagamento = LocalDate.now();
        var conta = new Conta();
        conta.setSituacao(SituacaoEnum.PENDENTE);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        assertDoesNotThrow(() -> contaService.pagar(id, dataPagamento));

        verify(contaRepository).findById(id);
        verify(contaRepository).save(conta);

        assertTrue(conta.isPaga());
    }

    @Test
    void testSalvarContaReturnsSavedEntity() {
        var conta = new Conta();
        when(contaRepository.save(conta)).thenReturn(conta);

        var result = contaService.salvar(conta);

        assertEquals(conta, result);
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    void testTotalPagoWithinDateRange() {
        var dataInicio = LocalDate.of(2023, 1, 1);
        var dataFim = LocalDate.of(2023, 12, 31);
        var expectedTotal = new BigDecimal("1000.00");

        when(contaRepository.totalPago(dataInicio, dataFim)).thenReturn(Optional.of(expectedTotal));

        var actualTotal = contaService.totalPago(dataInicio, dataFim);

        assertEquals(expectedTotal, actualTotal);
        verify(contaRepository, times(1)).totalPago(dataInicio, dataFim);
    }

    @Test
    void testTotalPago_InvalidDateRange() {
        var dataInicio = LocalDate.of(2023, 12, 31);
        var dataFim = LocalDate.of(2023, 1, 1);

        assertThrows(ContasPagarException.class, () -> contaService.totalPago(dataInicio, dataFim));
        verify(contaRepository, never()).totalPago(dataInicio, dataFim);
    }

    @Test
    void testTotalPago_NoAccountsFound() {
        var dataInicio = LocalDate.of(2023, 1, 1);
        var dataFim = LocalDate.of(2023, 12, 31);

        when(contaRepository.totalPago(dataInicio, dataFim)).thenReturn(Optional.empty());

        assertThrows(ContasPagarException.class, () -> contaService.totalPago(dataInicio, dataFim));
        verify(contaRepository, times(1)).totalPago(dataInicio, dataFim);
    }

    @Test
    void testTotalPago_NullDates() {
        assertThrows(ContasPagarException.class, () -> contaService.totalPago(null, LocalDate.now()));
        assertThrows(ContasPagarException.class, () -> contaService.totalPago(LocalDate.now(), null));
        verify(contaRepository, never()).totalPago(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testBuscarPorIdContaNotFound() {
        var id = UUID.randomUUID();
        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ContasPagarException.class, () -> contaService.buscarPorId(id));
        verify(contaRepository, times(1)).findById(id);
    }

    @Test
    void testBuscarPorIdWithNullId() {
        assertThrows(ContasPagarException.class, () -> contaService.buscarPorId(null));
        verify(contaRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testFindByVencimentoEDescricaoBothParametersNull() {
        LocalDate dataVencimento = null;
        String descricao = null;
        var pageRequest = PageRequest.of(0, 10);

        assertThrows(ContasPagarException.class, () ->
                contaService.findByVencimentoEDescricao(dataVencimento, descricao, pageRequest)
        );

        verifyNoInteractions(contaRepository);
    }

    @Test
    void testFindByVencimentoEDescricaoEmptyDescricao() {
        LocalDate dataVencimento = null;
        var descricao = "";
        var pageRequest = PageRequest.of(0, 10);

        assertThrows(ContasPagarException.class, () ->
                contaService.findByVencimentoEDescricao(dataVencimento, descricao, pageRequest)
        );

        verifyNoInteractions(contaRepository);
    }

    @Test
    void testFindByVencimentoEDescricaoNullPageRequest() {
        var dataVencimento = LocalDate.now();
        var descricao = "Test";
        PageRequest pageRequest = null;

        assertDoesNotThrow(() -> contaService.findByVencimentoEDescricao(dataVencimento, descricao, pageRequest));

        verify(contaRepository)
                .findByDataVencimentoAndDescricaoContainingIgnoreCase(any(), any(), any());
    }

    @Test
    void testFindByVencimentoEDescricaoThrowsExceptionWhenBothParametersAreNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        assertThrows(ContasPagarException.class, () ->
                contaService.findByVencimentoEDescricao(null, null, pageRequest)
        );
        verifyNoInteractions(contaRepository);
    }

    @Test
    void testFindByVencimentoEDescricaoWhenOnlyDataVencimentoProvided() {
        var dataVencimento = LocalDate.now();
        var pageRequest = PageRequest.of(0, 10);
        Page<Conta> expectedPage = new PageImpl<>(Collections.emptyList());

        when(contaRepository.findByDataVencimento(dataVencimento, pageRequest)).thenReturn(expectedPage);

        var result = contaService.findByVencimentoEDescricao(dataVencimento, null, pageRequest);

        assertEquals(expectedPage, result);
        verify(contaRepository, times(1)).findByDataVencimento(dataVencimento, pageRequest);
    }

    @Test
    void testFindByVencimentoEDescricaoWhenOnlyDescriptionProvided() {
        var descricao = "Test Description";
        var pageRequest = PageRequest.of(0, 10);
        Page<Conta> expectedPage = new PageImpl<>(Collections.emptyList());

        when(contaRepository.findByDescricaoContainingIgnoreCase(descricao, pageRequest)).thenReturn(expectedPage);

        var result = contaService.findByVencimentoEDescricao(null, descricao, pageRequest);

        assertEquals(expectedPage, result);
        verify(contaRepository, times(1)).findByDescricaoContainingIgnoreCase(descricao, pageRequest);
    }

    @Test
    void testFindByVencimentoEDescricaoWithBothParameters() {
        var dataVencimento = LocalDate.now();
        var descricao = "Test";
        var pageRequest = PageRequest.of(0, 10);
        Page<Conta> expectedPage = new PageImpl<>(new ArrayList<>());

        when(contaRepository.findByDataVencimentoAndDescricaoContainingIgnoreCase(dataVencimento, descricao, pageRequest))
                .thenReturn(expectedPage);

        var result = contaService.findByVencimentoEDescricao(dataVencimento, descricao, pageRequest);

        assertEquals(expectedPage, result);
        verify(contaRepository, times(1))
                .findByDataVencimentoAndDescricaoContainingIgnoreCase(dataVencimento, descricao, pageRequest);
    }

    @Test
    void testImportarContasCsv() {
        var csvContent = "2023-05-01,2023-05-01,100.50,Conta de luz,PAGA";
        var file = new MockMultipartFile("test.csv", csvContent.getBytes(StandardCharsets.UTF_8));
        var input = new CsvInput(file);
        assertDoesNotThrow(() -> contaService.importarContasCsv(input));

        verify(contaRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testListarTodasEmptyResult() {
        var pageRequest = PageRequest.of(0, 10);
        when(contaRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(Collections.emptyList()));

        assertThrows(ContasPagarException.class, () -> contaService.listarTodas(pageRequest));
        verify(contaRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testListarTodasNullPageRequest() {
        when(contaRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        PageRequest pageRequest = null;
        assertThrows(ContasPagarException.class, () -> contaService.listarTodas(pageRequest));
    }

    @Test
    void testListarTodasWhenContasExistReturnsPageOfContas() {
        var pageRequest = PageRequest.of(0, 10);
        var contaList = Arrays.asList(new Conta(), new Conta());
        var expectedPage = new PageImpl<>(contaList);
        when(contaRepository.findAll(pageRequest)).thenReturn(expectedPage);

        var result = contaService.listarTodas(pageRequest);

        assertEquals(expectedPage, result);
        verify(contaRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testPagarContaAlreadyPaid() {
        var id = UUID.randomUUID();
        var conta = new Conta();
        conta.setSituacao(SituacaoEnum.PAGA);
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        assertThrows(ContasPagarException.class, () -> contaService.pagar(id, LocalDate.now()));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void testPagarContaNotFound() {
        var id = UUID.randomUUID();
        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ContasPagarException.class, () -> contaService.pagar(id, LocalDate.now()));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void testPagarNullDate() {
        var id = UUID.randomUUID();
        when(contaRepository.findById(id)).thenReturn(Optional.of(new Conta()));
        assertThrows(ContasPagarException.class, () -> contaService.pagar(id, null));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void testPagarSaveException() {
        var id = UUID.randomUUID();
        var conta = new Conta();
        conta.setSituacao(SituacaoEnum.PENDENTE);
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(ContasPagarException.class, () -> contaService.pagar(id, LocalDate.now()));
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testSalvarWhenRepositoryThrowsException() {
        var conta = new Conta();
        when(contaRepository.save(any(Conta.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(ContasPagarException.class, () -> contaService.salvar(conta));
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

}
