package com.api.MoriMagazineAPI.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "parcela")
public class ParcelaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "transacao_id")
    @JsonBackReference // Evita loop infinito na serialização para o campo transacao
    @ToString.Exclude
    private TransacaoEntity transacao;

    private BigDecimal valorParcela;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    private StatusParcela status;

    private BigDecimal valor;

    private Integer restantes;

    public ParcelaEntity(TransacaoEntity transacao, BigDecimal valorParcela, LocalDate dataVencimento, StatusParcela status) {
        this.transacao = transacao;
        this.valorParcela = valorParcela;
        this.dataVencimento = dataVencimento;
        this.status = status;
    }

    // Método para verificar se a parcela está paga
    public boolean isPago() {
        return this.status == StatusParcela.PAGO;
    }

    // Método para marcar a parcela como paga
    public void marcarComoPago() {
        this.status = StatusParcela.PAGO;
    }
}