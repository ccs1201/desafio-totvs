package br.com.ccs.contaspagar.domain.repository;

import br.com.ccs.contaspagar.domain.entity.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID> {

    Page<Conta> findByDataVencimentoAndDescricaoContainingIgnoreCase(LocalDate dataVencimento, String descricao, Pageable pageable);

    Page<Conta> findByDataVencimento(LocalDate dataVencimento, Pageable pageable);

    Page<Conta> findByDescricaoContainingIgnoreCase(String descricao,Pageable pageable);

    @Query(value = "SELECT sum(c.valor) FROM Conta c WHERE c.dataPagamento BETWEEN :dataInicio AND :dataFim")
    Optional<BigDecimal> totalPago(LocalDate dataInicio, LocalDate dataFim);
}

