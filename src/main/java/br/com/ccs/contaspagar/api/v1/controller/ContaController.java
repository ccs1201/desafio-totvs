package br.com.ccs.contaspagar.api.v1.controller;

import br.com.ccs.contaspagar.api.v1.model.input.ContaInput;
import br.com.ccs.contaspagar.api.v1.model.input.CsvInput;
import br.com.ccs.contaspagar.api.v1.model.output.ContaOutput;
import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/contas", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Tag(name = "Contas a Pagar", description = "API para controle de contas a pagar")
class ContaController {
    private final ContaService contaService;

    @Operation(summary = "Criar uma conta")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaOutput criarConta(@RequestBody @Valid ContaInput contaInput) {
        return ContaOutput.fromEntity(contaService.salvar(contaInput.toConta()));
    }

    @Operation(summary = "Listar todas as contas")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ContaOutput> listarContas(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                          @RequestParam(defaultValue = "5") @Positive int size,
                                          @RequestParam(defaultValue = "dataVencimento") String sort,
                                          @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        return ContaOutput
                .fromPage(contaService
                        .listarTodas(PageRequest
                                .of(page, size, direction, sort)));

    }

    @Operation(summary = "Buscar conta por id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContaOutput buscarConta(@PathVariable @NotNull UUID id) {
        return ContaOutput.fromEntity(contaService.buscarPorId(id));
    }

    @Operation(summary = "Atualizar conta")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContaOutput atualizarConta(@PathVariable UUID id, @RequestBody @Valid ContaInput contaInput) {
        Conta conta = contaInput.toConta();
        conta.setId(id);
        return ContaOutput.fromEntity(contaService.salvar(conta));
    }

    @Operation(summary = "Pagar conta")
    @PatchMapping("/{id}/pagamento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pagarConta(@PathVariable UUID id, @RequestParam @NotNull LocalDate dataPagamento) {
        contaService.pagar(id, dataPagamento);
    }

    @Operation(summary = "Cancelar conta")
    @PatchMapping("/{id}/cancelamento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarConta(@PathVariable @NotNull UUID id) {
        contaService.cancelar(id);
    }


    @Operation(summary = "Obter contas por Data de Vencimento e/ou Descrição")
    @GetMapping("/filtro")
    @ResponseStatus(HttpStatus.OK)
    public Page<ContaOutput> filtro(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                    @RequestParam(defaultValue = "5") @Positive int size,
                                    @RequestParam(defaultValue = "dataVencimento") String sort,
                                    @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                    @RequestParam(required = false) LocalDate dataVencimento,
                                    @RequestParam(required = false) String descricao) {
        return ContaOutput
                .fromPage(contaService
                        .findByVencimentoEDescricao(dataVencimento, descricao, PageRequest
                                .of(page, size, direction, sort)));
    }

    @Operation(summary = "Obter valor total pago num determinado período")
    @GetMapping("/totalPago")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal obterValorTotalPago(@RequestParam @NotNull LocalDate dataInicio, @RequestParam @NotNull LocalDate dataFim) {
        return contaService.totalPago(dataInicio, dataFim);
    }

    @Operation(summary = "Importar contas a partir de um arquivo CSV")
    @PostMapping("/importacaocsv")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ContaOutput> importarContas(@Valid CsvInput csvInput) {
        return ContaOutput.fromEntityList(contaService.importarContasCsv(csvInput));
    }
}
