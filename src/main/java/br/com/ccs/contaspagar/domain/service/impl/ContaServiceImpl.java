package br.com.ccs.contaspagar.domain.service.impl;

import br.com.ccs.contaspagar.api.v1.model.input.CsvInput;
import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.repository.ContaRepository;
import br.com.ccs.contaspagar.domain.service.ContaService;
import br.com.ccs.contaspagar.domain.util.ContaCsvReader;
import br.com.ccs.contaspagar.domain.vo.SituacaoEnum;
import br.com.ccs.contaspagar.infra.exception.ContasPagarException;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {
    private final ContaRepository contaRepository;

    @Transactional
    @Override
    public Conta salvar(Conta conta) {
        return save(conta);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Conta> listarTodas(PageRequest pageRequest) {
        pageRequest = checkPageRequest(pageRequest);

        var contas = contaRepository.findAll(pageRequest);

        if (contas.isEmpty()) {
            throw new ContasPagarException(NOT_FOUND, "Nenhuma conta encontrada.");
        }
        return contas;
    }

    @Transactional(readOnly = true)
    @Override
    public Conta buscarPorId(UUID id) {
        return findById(id);
    }

    @Transactional
    @Override
    public void pagar(UUID id, LocalDate dataPagamento) {
        var conta = findById(id);

        if (isNull(dataPagamento)) {
            throw new ContasPagarException(BAD_REQUEST, "Data de pagamento não pode ser nula.");
        }

        if (conta.getSituacao() == SituacaoEnum.PAGA) {
            throw new ContasPagarException(BAD_REQUEST, "Conta já esta paga.");
        }

        conta.pagar(dataPagamento);
        save(conta);
    }

    @Transactional
    @Override
    public void cancelar(UUID id) {
        var conta = findById(id);
        if (conta.getSituacao() == SituacaoEnum.PAGA) {
            throw new ContasPagarException(BAD_REQUEST, "Conta já esta paga e não pode ser cancelada.");
        }
        conta.cancelar();
        save(conta);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Conta> findByVencimentoEDescricao(LocalDate dataVencimento, String descricao, PageRequest pageRequest) {

        pageRequest = checkPageRequest(pageRequest);

        if (nonNull(dataVencimento) && nonNull(descricao)) {
            return contaRepository.findByDataVencimentoAndDescricaoContainingIgnoreCase(dataVencimento, descricao, pageRequest);
        }
        if (nonNull(dataVencimento)) {
            return contaRepository.findByDataVencimento(dataVencimento, pageRequest);
        }
        if (isNotBlank(descricao)) {
            return contaRepository.findByDescricaoContainingIgnoreCase(descricao, pageRequest);
        }

        throw new ContasPagarException(BAD_REQUEST, "Informe ao menos um dos parâmetros: Data Vencimento ou Descrição ");
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal totalPago(LocalDate dataInicio, LocalDate dataFim) {

        if (isNull(dataInicio) || isNull(dataFim)) {
            throw new ContasPagarException(BAD_REQUEST, "Data de início e fim não podem ser nulas.");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new ContasPagarException(BAD_REQUEST, "Data de início não pode ser maior que a data fim.");
        }

        return contaRepository
                .totalPago(dataInicio, dataFim)
                .orElseThrow(() -> new ContasPagarException(NOT_FOUND, "Nenhuma conta encontrada para o período informado."));
    }

    @Transactional
    @Override
    public Collection<Conta> importarContasCsv(CsvInput csvInput) {
        return contaRepository.saveAll(ContaCsvReader.readCsv(csvInput));
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    private Conta findById(UUID id) {
        if (isNull(id)) {
            throw new ContasPagarException(BAD_REQUEST, "Id não pode ser nulo.");
        }
        return contaRepository.findById(id).orElseThrow(() ->
                new ContasPagarException(NOT_FOUND, "Conta não encontrada para o id:".concat(id.toString())));
    }

    private Conta save(Conta conta) {
        try {
            return contaRepository.save(conta);
        } catch (Exception e) {
            throw new ContasPagarException(BAD_REQUEST, "Erro ao salvar conta: "
                    .concat(conta.toString()), e);
        }
    }
}
