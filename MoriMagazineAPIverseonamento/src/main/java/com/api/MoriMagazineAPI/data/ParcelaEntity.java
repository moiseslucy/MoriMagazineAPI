package com.api.MoriMagazineAPI.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "parcela")
public class ParcelaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transacao_id")
    @JsonBackReference
    private TransacaoEntity transacao;

    private BigDecimal valorParcela;
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    private StatusParcela status;

    private BigDecimal valor;  // Adicione este campo para armazenar o valor total

    public ParcelaEntity(TransacaoEntity transacao, BigDecimal valorParcela, LocalDate dataVencimento, StatusParcela status, BigDecimal valor) {
        this.transacao = transacao;
        this.valorParcela = valorParcela;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.valor = valor;
    }
}
