package com.api.MoriMagazineAPI.data;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transacao")
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    @NotNull(message = "O cliente é obrigatório")
    private ClienteEntity cliente;

    @JsonProperty("data_transacao")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "A data da transação é obrigatória")
    private LocalDate dataTransacao;

    @JsonProperty("valor_total")
    @NotNull(message = "O valor total é obrigatório")
    private BigDecimal valorTotal;

    @NotBlank(message = "O status da transação é obrigatório")
    private String status;

    @JsonProperty("forma_pagamento")
    @NotBlank(message = "A forma de pagamento é obrigatória")
    private String formaPagamento;

    private String numeroCartao;
    private String chavePix;
    private Integer numeroParcelas;

    @JsonProperty("data_vencimento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataVencimento;

    @ElementCollection
    private List<Long> produtosIds = new ArrayList<>();

    @OneToMany(mappedBy = "transacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ParcelaEntity> parcelas = new ArrayList<>();

    @OneToMany(mappedBy = "transacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemTransacao> itens = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "transacao_produto", 
        joinColumns = @JoinColumn(name = "transacao_id"), 
        inverseJoinColumns = @JoinColumn(name = "produto_id"))
    private List<ProdutoEntity> produtos = new ArrayList<>();

    public TransacaoEntity(ClienteEntity cliente, BigDecimal valorTotal, LocalDate dataTransacao, String formaPagamento) {
        this.cliente = cliente;
        this.valorTotal = valorTotal;
        this.dataTransacao = dataTransacao;
        this.status = "Concluída";
        this.formaPagamento = formaPagamento;
    }

    public TransacaoEntity(ClienteEntity cliente, BigDecimal valorTotal, LocalDate dataTransacao, String formaPagamento, int numeroParcelas, LocalDate dataVencimento) {
        this(cliente, valorTotal, dataTransacao, formaPagamento);
        if (numeroParcelas <= 0) {
            throw new IllegalArgumentException("O número de parcelas deve ser maior que zero");
        }
        this.numeroParcelas = numeroParcelas;
        this.dataVencimento = dataVencimento;
        criarParcelas(valorTotal, numeroParcelas, dataVencimento);
    }

    public void criarParcelas(BigDecimal valorTotal, int numeroParcelas, LocalDate dataVencimento) {
        if (dataVencimento == null) {
            throw new IllegalArgumentException("A data de vencimento não pode ser nula");
        }
        BigDecimal valorParcela = valorTotal.divide(new BigDecimal(numeroParcelas), 2, RoundingMode.HALF_UP);
        for (int i = 0; i < numeroParcelas; i++) {
            ParcelaEntity parcela = new ParcelaEntity(this, valorParcela, dataVencimento.plusMonths(i), StatusParcela.PENDENTE, valorTotal);
            parcelas.add(parcela);
        }
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public List<Long> getProdutosIds() {
        return produtos.stream().map(ProdutoEntity::getId).collect(Collectors.toList());
    }

    public void setProdutos(List<ProdutoEntity> produtos) {
        this.produtos = produtos;
        this.produtosIds = produtos.stream().map(ProdutoEntity::getId).collect(Collectors.toList());
    }

    public void setProdutosIds(List<Long> produtosIds) {
        this.produtosIds = produtosIds;
    }
}
