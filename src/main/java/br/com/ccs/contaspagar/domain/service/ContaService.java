package br.com.ccs.contaspagar.domain.service;

import br.com.ccs.contaspagar.api.v1.model.input.CsvInput;
import br.com.ccs.contaspagar.domain.entity.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface ContaService {
    @Transactional
    Conta salvar(Conta conta);

    @Transactional(readOnly = true)
    Page<Conta> listarTodas(PageRequest pageRequest);

    @Transactional(readOnly = true)
    Conta buscarPorId(UUID id);

    @Transactional
    void pagar(UUID id, LocalDate dataPagamento);

    @Transactional
    void cancelar(UUID id);

    @Transactional(readOnly = true)
    Page<Conta> findByVencimentoEDescricao(LocalDate dataVencimento, String descricao, PageRequest pageRequest);

    BigDecimal totalPago(LocalDate dataInicio, LocalDate dataFim);

    @Transactional
    void importarContasCsv(CsvInput csvInput);
}
