package br.com.ccs.contaspagar.api.v1.model.input;

import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.vo.SituacaoEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContaInput(
        @FutureOrPresent(message = "Data de vencimento não pode ser anterior a data atual.") @NotNull(message = "Não pode estar vazio") LocalDate dataVencimento,
        LocalDate dataPagamento,
        @Positive(message = "Deve ser maior que zero") @NotNull(message = "Não pode estar vazio") BigDecimal valor,
        @NotBlank(message = "Não pode estar vazio") String descricao,
        @NotNull(message = "Não pode estar vazio") SituacaoEnum situacao) {

    public Conta toConta() {
        return Conta.builder()
                .dataVencimento(dataVencimento)
                .dataPagamento(dataPagamento)
                .valor(valor)
                .descricao(descricao)
                .situacao(situacao)
                .build();
    }
}