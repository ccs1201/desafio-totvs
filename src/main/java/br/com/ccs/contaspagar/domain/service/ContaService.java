package br.com.ccs.contaspagar.domain.service;

import br.com.ccs.contaspagar.api.v1.model.input.CsvInput;
import br.com.ccs.contaspagar.domain.core.exception.ContasPagarServiceException;
import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.repository.ContaRepository;
import br.com.ccs.contaspagar.domain.util.ContaCsvReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContaService {
    private final ContaRepository contaRepository;

    @Transactional
    public Conta salvar(Conta conta) {
        try {
            return contaRepository.save(conta);
        } catch (Exception e) {
            throw new ContasPagarServiceException("Erro ao salvar conta: "
                    .concat(conta.toString()), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Conta> listarTodas(PageRequest pageRequest) {
        var contas = contaRepository.findAll(pageRequest);

        if (contas.isEmpty()) {
            throw new ContasPagarServiceException("Nenhuma conta encontrada.");
        }
        return contas;
    }

    @Transactional(readOnly = true)
    public Conta buscarPorId(UUID id) {
        return contaRepository.findById(id).orElseThrow(() ->
                new ContasPagarServiceException("Conta não encontrada para o id:".concat(id.toString())));
    }

    @Transactional
    public void pagar(UUID id, LocalDate dataPagamento) {
        var conta = buscarPorId(id);
        conta.pagar(dataPagamento);
        salvar(conta);
    }

    @Transactional
    public void cancelar(UUID id) {
        var conta = buscarPorId(id);
        conta.cancelar();
        salvar(conta);
    }

    @Transactional(readOnly = true)
    public Page<Conta> findByVencimentoEDescricao(LocalDate dataVencimento, String descricao, PageRequest pageRequest) {
        if (dataVencimento != null && descricao != null) {
            return contaRepository.findByDataVencimentoAndDescricaoContainingIgnoreCase(dataVencimento, descricao, pageRequest);
        }
        if (dataVencimento != null) {
            return contaRepository.findByDataVencimento(dataVencimento, pageRequest);
        }
        if (descricao != null) {
            return contaRepository.findByDescricaoContainingIgnoreCase(descricao, pageRequest);
        }

        throw new ContasPagarServiceException("Informe ao menos um dos parâmetros: Data Vencimento ou Descrição ");
    }

    public BigDecimal totalPago(LocalDate dataInicio, LocalDate dataFim) {
        return contaRepository
                .totalPago(dataInicio, dataFim)
                .orElseThrow(() -> new ContasPagarServiceException("Nenhuma conta encontrada para o período informado."));
    }

    @Transactional
    public void importarContasCsv(CsvInput csvInput) {
        contaRepository.saveAll(ContaCsvReader.readCsv(csvInput));

    }
}
