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

    /**
     * Test case for buscarPorId method when a valid UUID is provided.
     * It should return the Conta object found by the repository.
     */
    @Test
    void testBuscarPorIdReturnsContaWhenFound() {
        UUID id = UUID.randomUUID();
        Conta expectedConta = new Conta();
        when(contaRepository.findById(id)).thenReturn(Optional.of(expectedConta));

        Conta result = contaService.buscarPorId(id);

        assertEquals(expectedConta, result);
        verify(contaRepository, times(1)).findById(id);
    }

    /**
     * Test case for canceling a paid account.
     * Verifies that attempting to cancel a paid account throws a ContasPagarException.
     */
    @Test
    void testCancelarContaPaga() {
        UUID id = UUID.randomUUID();
        Conta conta = new Conta();
        conta.setSituacao(SituacaoEnum.PAGA);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        assertThrows(ContasPagarException.class, () -> contaService.cancelar(id));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    /**
     * Test canceling a conta that does not exist.
     * This tests the scenario where the input is valid but the conta is not found.
     */
    @Test
    void testCancelarContaNaoExistente() {
        UUID id = UUID.randomUUID();
        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ContasPagarException.class, () -> contaService.cancelar(id));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    /**
     * Test canceling a conta when the save operation fails.
     * This tests the exception handling when the repository throws an exception.
     */
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

    /**
     * Test canceling a conta with a null UUID.
     * This tests the scenario where the input is invalid (null).
     */
    @Test
    void testCancelarWithNullId() {
        assertThrows(ContasPagarException.class, () -> contaService.cancelar(null));
        verify(contaRepository, never()).findById(any(UUID.class));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    /**
     * Test case for listarTodas method when no accounts are found.
     * Verifies that a ContasPagarException is thrown with the correct message.
     */
    @Test
    void testListarTodasWhenNoAccountsFound() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(contaRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(new ArrayList<>()));

        var exception = assertThrows(ContasPagarException.class,
                () -> contaService.listarTodas(pageRequest));

        assertEquals("Nenhuma conta encontrada.", exception.getMessage());
        verify(contaRepository, times(1)).findAll(pageRequest);
    }

    /**
     * Test case for pagar method when the account is already paid.
     * This test verifies that a ContasPagarException is thrown when
     * attempting to pay an account that is already in PAGA (paid) status.
     */
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

    /**
     * Test case for pagar method when the account is not already paid.
     * This test verifies that the pagar method successfully processes the payment
     * for an account that is not in the PAGA (paid) status.
     */
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

    /**
     * Test case for salvar method in ContaServiceImpl
     * Verifies that the method correctly saves a Conta object and returns the saved entity
     */
    @Test
    void testSalvarContaReturnsSavedEntity() {
        var conta = new Conta();
        when(contaRepository.save(conta)).thenReturn(conta);

        var result = contaService.salvar(conta);

        assertEquals(conta, result);
        verify(contaRepository, times(1)).save(conta);
    }

    /**
     * Test case for totalPago method when there are paid accounts within the specified date range.
     * It verifies that the method returns the correct total amount paid.
     */
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

    /**
     * Test case for totalPago method when the start date is after the end date.
     * This test verifies that an ContasPagarException is thrown when the input dates are invalid.
     */
    @Test
    void testTotalPago_InvalidDateRange() {
        var dataInicio = LocalDate.of(2023, 12, 31);
        var dataFim = LocalDate.of(2023, 1, 1);

        assertThrows(ContasPagarException.class, () -> contaService.totalPago(dataInicio, dataFim));
        verify(contaRepository, never()).totalPago(dataInicio, dataFim);
    }

    /**
     * Test case for totalPago method when no accounts are found for the given period.
     * This test verifies that a ContasPagarException is thrown when the repository
     * returns an empty Optional.
     */
    @Test
    void testTotalPago_NoAccountsFound() {
        var dataInicio = LocalDate.of(2023, 1, 1);
        var dataFim = LocalDate.of(2023, 12, 31);

        when(contaRepository.totalPago(dataInicio, dataFim)).thenReturn(Optional.empty());

        assertThrows(ContasPagarException.class, () -> contaService.totalPago(dataInicio, dataFim));
        verify(contaRepository, times(1)).totalPago(dataInicio, dataFim);
    }

    /**
     * Test case for totalPago method when null dates are provided.
     * This test verifies that a ContasPagarException is thrown when either start or end date is null.
     */
    @Test
    void testTotalPago_NullDates() {
        assertThrows(ContasPagarException.class, () -> contaService.totalPago(null, LocalDate.now()));
        assertThrows(ContasPagarException.class, () -> contaService.totalPago(LocalDate.now(), null));
        verify(contaRepository, never()).totalPago(any(LocalDate.class), any(LocalDate.class));
    }

    /**
     * Test case for buscarPorId method when the conta is not found.
     * This test verifies that a ContasPagarException is thrown when the repository returns an empty Optional.
     */
    @Test
    void test_buscarPorId_contaNotFound() {
        var id = UUID.randomUUID();
        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ContasPagarException.class, () -> contaService.buscarPorId(id));
        verify(contaRepository, times(1)).findById(id);
    }

    /**
     * Test case for buscarPorId method when the input ID is null.
     * This test verifies that a ContasPagarException is thrown when a null UUID is provided.
     */
    @Test
    void test_buscarPorId_withNullId() {
        assertThrows(ContasPagarException.class, () -> contaService.buscarPorId(null));
        verify(contaRepository, never()).findById(any(UUID.class));
    }

    /**
     * Negative test case for findByVencimentoEDescricao when both dataVencimento and descricao are null.
     * This should throw a ContasPagarException.
     */
    @Test
    void test_findByVencimentoEDescricao_bothParametersNull() {
        LocalDate dataVencimento = null;
        String descricao = null;
        var pageRequest = PageRequest.of(0, 10);

        assertThrows(ContasPagarException.class, () ->
                contaService.findByVencimentoEDescricao(dataVencimento, descricao, pageRequest)
        );

        verifyNoInteractions(contaRepository);
    }

    /**
     * Negative test case for findByVencimentoEDescricao when descricao is an empty string.
     * This should be treated as a null descricao and throw a ContasPagarException when dataVencimento is also null.
     */
    @Test
    void test_findByVencimentoEDescricao_emptyDescricao() {
        LocalDate dataVencimento = null;
        var descricao = "";
        var pageRequest = PageRequest.of(0, 10);

        assertThrows(ContasPagarException.class, () ->
                contaService.findByVencimentoEDescricao(dataVencimento, descricao, pageRequest)
        );

        verifyNoInteractions(contaRepository);
    }

    /**
     * Test case for findByVencimentoEDescricao when pageRequest is null.
     * This should not throw an Exception
     */
    @Test
    void test_findByVencimentoEDescricao_nullPageRequest() {
        var dataVencimento = LocalDate.now();
        var descricao = "Test";
        PageRequest pageRequest = null;

        assertDoesNotThrow(() -> contaService.findByVencimentoEDescricao(dataVencimento, descricao, pageRequest));

        verify(contaRepository)
                .findByDataVencimentoAndDescricaoContainingIgnoreCase(any(), any(), any());
    }

    /**
     * Test case for findByVencimentoEDescricao method when both dataVencimento and descricao are null.
     * This should throw a ContasPagarException.
     */
    @Test
    void test_findByVencimentoEDescricao_throwsExceptionWhenBothParametersAreNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        assertThrows(ContasPagarException.class, () ->
                contaService.findByVencimentoEDescricao(null, null, pageRequest)
        );
        verifyNoInteractions(contaRepository);
    }

    /**
     * Test case for findByVencimentoEDescricao when only dataVencimento is provided.
     * This test verifies that the method correctly calls findByDataVencimento on the repository
     * when dataVencimento is not null and descricao is null.
     */
    @Test
    void test_findByVencimentoEDescricao_whenOnlyDataVencimentoProvided() {
        var dataVencimento = LocalDate.now();
        var pageRequest = PageRequest.of(0, 10);
        Page<Conta> expectedPage = new PageImpl<>(Collections.emptyList());

        when(contaRepository.findByDataVencimento(dataVencimento, pageRequest)).thenReturn(expectedPage);

        var result = contaService.findByVencimentoEDescricao(dataVencimento, null, pageRequest);

        assertEquals(expectedPage, result);
        verify(contaRepository, times(1)).findByDataVencimento(dataVencimento, pageRequest);
    }

    /**
     * Test case for findByVencimentoEDescricao method when only description is provided.
     * This test verifies that the method correctly calls the repository method
     * findByDescricaoContainingIgnoreCase when dataVencimento is null and descricao is not null.
     */
    @Test
    void test_findByVencimentoEDescricao_whenOnlyDescriptionProvided() {
        var descricao = "Test Description";
        var pageRequest = PageRequest.of(0, 10);
        Page<Conta> expectedPage = new PageImpl<>(Collections.emptyList());

        when(contaRepository.findByDescricaoContainingIgnoreCase(descricao, pageRequest)).thenReturn(expectedPage);

        var result = contaService.findByVencimentoEDescricao(null, descricao, pageRequest);

        assertEquals(expectedPage, result);
        verify(contaRepository, times(1)).findByDescricaoContainingIgnoreCase(descricao, pageRequest);
    }

    /**
     * Tests the findByVencimentoEDescricao method when both dataVencimento and descricao are provided.
     * Verifies that the method correctly calls the repository method with the given parameters.
     */
    @Test
    void test_findByVencimentoEDescricao_withBothParameters() {
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

    /**
     * Test case for importing accounts from CSV with invalid input format.
     * This test verifies that the method throws an exception when the input format is invalid.
     */
    @Test
    void test_importarContasCsv() {
        var csvContent = "2023-05-01,2023-05-01,100.50,Conta de luz,PAGA";
        var file = new MockMultipartFile("test.csv", csvContent.getBytes(StandardCharsets.UTF_8));
        var input = new CsvInput(file);
        assertDoesNotThrow(() -> contaService.importarContasCsv(input));

        verify(contaRepository, times(1)).saveAll(anyList());
    }

    /**
     * Tests the scenario where no accounts are found, resulting in a ContasPagarException.
     */
    @Test
    void test_listarTodas_emptyResult() {
        var pageRequest = PageRequest.of(0, 10);
        when(contaRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(Collections.emptyList()));

        assertThrows(ContasPagarException.class, () -> contaService.listarTodas(pageRequest));
        verify(contaRepository, times(1)).findAll(pageRequest);
    }

    /**
     * Tests the scenario where a null PageRequest is provided
     */
    @Test
    void test_listarTodas_nullPageRequest() {
        when(contaRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        PageRequest pageRequest = null;
        assertThrows(ContasPagarException.class, () -> contaService.listarTodas(pageRequest));
    }

    /**
     * Test case for listarTodas method when contas are not empty.
     * Verifies that the method returns the page of contas when they exist.
     */
    @Test
    void test_listarTodas_whenContasExist_returnsPageOfContas() {
        var pageRequest = PageRequest.of(0, 10);
        var contaList = Arrays.asList(new Conta(), new Conta());
        var expectedPage = new PageImpl<>(contaList);
        when(contaRepository.findAll(pageRequest)).thenReturn(expectedPage);

        var result = contaService.listarTodas(pageRequest);

        assertEquals(expectedPage, result);
        verify(contaRepository, times(1)).findAll(pageRequest);
    }

    /**
     * Test case for pagar method when the conta is already paid.
     * This test verifies that the method throws a ContasPagarException when trying to pay an already paid conta.
     */
    @Test
    void test_pagar_conta_already_paid() {
        var id = UUID.randomUUID();
        var conta = new Conta();
        conta.setSituacao(SituacaoEnum.PAGA);
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        assertThrows(ContasPagarException.class, () -> contaService.pagar(id, LocalDate.now()));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    /**
     * Test case for pagar method when the conta is not found.
     * This test verifies that the method throws a ContasPagarNotFoundException when the conta is not found.
     */
    @Test
    void test_pagar_conta_not_found() {
        var id = UUID.randomUUID();
        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ContasPagarException.class, () -> contaService.pagar(id, LocalDate.now()));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    /**
     * Test case for pagar method when the input date is null.
     * This test verifies that the method throws a ContasPagarException when the date is null.
     */
    @Test
    void test_pagar_null_date() {
        var id = UUID.randomUUID();
        when(contaRepository.findById(id)).thenReturn(Optional.of(new Conta()));
        assertThrows(ContasPagarException.class, () -> contaService.pagar(id, null));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    /**
     * Test case for pagar method when an exception occurs during saving.
     * This test verifies that the method throws a ContasPagarException when an error occurs while saving the conta.
     */
    @Test
    void test_pagar_save_exception() {
        var id = UUID.randomUUID();
        var conta = new Conta();
        conta.setSituacao(SituacaoEnum.PENDENTE);
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(ContasPagarException.class, () -> contaService.pagar(id, LocalDate.now()));
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    /**
     * Test case for salvar method when repository throws an exception
     * This test verifies that the salvar method properly handles and wraps
     * any exception thrown by the repository's save method.
     */
    @Test
    void test_salvar_when_repository_throws_exception() {
        var conta = new Conta();
        when(contaRepository.save(any(Conta.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(ContasPagarException.class, () -> contaService.salvar(conta));
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

}
