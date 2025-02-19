package br.com.ccs.contaspagar.api.v1.model.input;

import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.vo.Situacao;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContaInput(@FutureOrPresent @NotNull LocalDate dataVencimento,
                         LocalDate dataPagamento,
                         @Positive @NotNull BigDecimal valor,
                         @NotBlank String descricao,
                         @NotNull Situacao situacao) {
    public Conta toEntity() {
        return Conta.builder()
                .dataVencimento(dataVencimento)
                .dataPagamento(dataPagamento)
                .valor(valor)
                .descricao(descricao)
                .situacao(situacao)
                .build();
    }
}