package br.com.ccs.contaspagar.api.v1.model.output;

import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.vo.Situacao;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record ContaOutput(UUID id, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
                          String descricao,
                          Situacao situacao,
                          OffsetDateTime dataBaixa,
                          OffsetDateTime dataCriacao,
                          OffsetDateTime dataAtualizacao,
                          boolean importadaViaCsv) {
    public static ContaOutput fromEntity(Conta conta) {
        return new ContaOutput(conta.getId(), conta.getDataVencimento(), conta.getDataPagamento(),
                conta.getValor(), conta.getDescricao(), conta.getSituacao(), conta.getDataBaixa(),
                conta.getDataCriacao(), conta.getDataAtualizacao(), conta.isImportadaViaCsv());
    }

    public static Page<ContaOutput> fromPage(Page<Conta> contas) {
        return contas.map(ContaOutput::fromEntity);
    }

    public static List<ContaOutput> fromEntityList(Collection<Conta> contas) {
        return contas.stream().map(ContaOutput::fromEntity).toList();
    }
}