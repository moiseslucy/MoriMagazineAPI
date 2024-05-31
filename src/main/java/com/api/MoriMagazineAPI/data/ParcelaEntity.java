package com.api.MoriMagazineAPI.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parcela")
public class ParcelaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transacao_id", nullable = false)
    private TransacaoEntity transacao;

    @NotNull(message = "O valor da parcela é obrigatório")
    private BigDecimal valor;

    @NotNull(message = "A data de vencimento da parcela é obrigatória")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O status da parcela é obrigatório")
    private StatusParcela status;

    // Construtor sem o ID
    public ParcelaEntity(TransacaoEntity transacao, BigDecimal valor, LocalDate dataVencimento, StatusParcela status) {
        this.transacao = transacao;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = status;
    }
}
