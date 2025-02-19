package br.com.ccs.contaspagar.domain.entity;

import br.com.ccs.contaspagar.domain.vo.Situacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "conta_pagar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "data_baixa")
    private OffsetDateTime dataBaixa;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Situacao situacao;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime dataAtualizacao;

    @Column(name = "importada_via_csv", nullable = false)
    private boolean importadaViaCsv = false;

    public void pagar(LocalDate dataPagamento) {
        this.situacao = Situacao.PAGA;
        this.dataPagamento = dataPagamento;
        this.dataBaixa = OffsetDateTime.now();
    }

    public void cancelar() {
        this.situacao = Situacao.CANCELADA;
        this.dataBaixa = OffsetDateTime.now();
    }
}