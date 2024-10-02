package com.api.MoriMagazineAPI.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data  // Lombok: Gera getters, setters, equals, hashCode e toString
@NoArgsConstructor // Lombok: Gera um construtor sem argumentos
@AllArgsConstructor // Lombok: Gera um construtor com todos os argumentos
@Entity
@Table(name = "item_transacao")
public class ItemTransacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transacao_id", nullable = false)
    private TransacaoEntity transacao;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;

    @NotNull
    private Integer quantidade;

    @NotNull
    private BigDecimal precoUnitario;

    @NotNull
    private BigDecimal subtotal;

    // Construtor para facilitar a criação e cálculo do subtotal
    public ItemTransacao(TransacaoEntity transacao, ProdutoEntity produto, Integer quantidade) {
        this.transacao = transacao;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
        this.subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}

