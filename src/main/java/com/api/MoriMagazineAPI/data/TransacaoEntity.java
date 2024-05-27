package com.api.MoriMagazineAPI.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transacao")
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID agora é do tipo Long

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @NotNull(message = "Data da transação obrigatória")
    private LocalDate dataTransacao;

    @NotNull(message = "Valor total obrigatório")
    private BigDecimal valorTotal;

    @NotBlank(message = "Status da transação obrigatório")
    private String status;

    @NotBlank(message = "Forma de pagamento obrigatória")
    private String formaPagamento;

    @ElementCollection
    @CollectionTable(name = "transacao_produto", joinColumns = @JoinColumn(name = "transacao_id"))
    @Column(name = "produto_id")
    private List<Long> produtosIds = new ArrayList<>(); // Inicialize aqui para garantir que nunca será nulo

    private String numeroCartao; 
    private int numeroParcelas; 
    private LocalDate dataPagamento; 
    private BigDecimal valorPago; 
    private BigDecimal valorParcela; 
    private LocalDate dataVencimento; 
    private String chavePix;

    // Construtor com parâmetros
    public TransacaoEntity(Long id, ClienteEntity cliente, LocalDate dataTransacao, BigDecimal valorTotal, String status, String formaPagamento, List<Long> produtosIds, String numeroCartao, int numeroParcelas, LocalDate dataPagamento, BigDecimal valorPago, BigDecimal valorParcela, LocalDate dataVencimento, String chavePix) {
        this.id = id;
        this.cliente = cliente;
        this.dataTransacao = dataTransacao;
        this.valorTotal = valorTotal;
        this.status = status;
        this.formaPagamento = formaPagamento;
        this.produtosIds = produtosIds != null ? produtosIds : new ArrayList<>(); // Inicialize para garantir que nunca será nulo
        this.numeroCartao = numeroCartao;
        this.numeroParcelas = numeroParcelas;
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
        this.valorParcela = valorParcela;
        this.dataVencimento = dataVencimento;
        this.chavePix = chavePix;
    }
}
