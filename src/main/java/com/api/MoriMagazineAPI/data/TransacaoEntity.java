package com.api.MoriMagazineAPI.data;

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
import java.util.Optional;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transacao")
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @NotNull(message = "O cliente é obrigatório")
    private ClienteEntity cliente;

    @NotNull(message = "A data da transação é obrigatória")
    private LocalDate dataTransacao;

    @NotNull(message = "O valor total é obrigatório")
    private BigDecimal valorTotal;

    @NotBlank(message = "O status da transação é obrigatório")
    private String status;

    @NotBlank(message = "A forma de pagamento é obrigatória")
    private String formaPagamento;

    @OneToMany(mappedBy = "transacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemTransacao> itens = new ArrayList<>();

    private String numeroCartao;

    private String chavePix;

    private Integer numeroParcelas;

    private LocalDate dataVencimento;

    @OneToMany(mappedBy = "transacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParcelaEntity> parcelas = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "transacao_produto", joinColumns = @JoinColumn(name = "transacao_id"))
    @Column(name = "produto_id")
    private List<Long> produtosIds = new ArrayList<>();

    public TransacaoEntity(ClienteEntity cliente, BigDecimal valorTotal, LocalDate dataTransacao, String formaPagamento) {
        this.cliente = cliente;
        this.valorTotal = valorTotal;
        this.dataTransacao = dataTransacao;
        this.status = "Concluída";
        this.formaPagamento = formaPagamento;
    }

    public TransacaoEntity(ClienteEntity cliente, BigDecimal valorTotal, LocalDate dataTransacao, String formaPagamento, int numeroParcelas, LocalDate dataVencimento) {
        this(cliente, valorTotal, dataTransacao, formaPagamento);
        this.numeroParcelas = numeroParcelas;
        this.dataVencimento = dataVencimento;
        criarParcelas(valorTotal, numeroParcelas, dataVencimento);
    }

    public void criarParcelas(BigDecimal valorTotal, int numeroParcelas, LocalDate dataVencimento) {
        BigDecimal valorParcela = valorTotal.divide(new BigDecimal(numeroParcelas), 2, RoundingMode.HALF_UP);
        for (int i = 0; i < numeroParcelas; i++) {
            ParcelaEntity parcela = new ParcelaEntity(this, valorParcela, dataVencimento.plusMonths(i), StatusParcela.PENDENTE);
            parcelas.add(parcela);
        }
    }

    public int getNumeroParcelas() {
        return parcelas.size();
    }

    public LocalDate getDataVencimento() {
        Optional<ParcelaEntity> proximaParcela = parcelas.stream()
                .filter(parcela -> parcela.getStatus() == StatusParcela.PENDENTE)
                .findFirst();

        return proximaParcela.map(ParcelaEntity::getDataVencimento).orElse(null);
    }

    public void setProdutosIds(List<Long> produtosIds) {
        this.produtosIds = produtosIds;
    }

    public List<Long> getProdutosIds() {
        return this.produtosIds;
    }
 @Transient // Este campo não será persistido no banco de dados
    private List<ProdutoEntity> produtos;

    // Getter e Setter para produtos
    public List<ProdutoEntity> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoEntity> produtos) {
        this.produtos = produtos;
    }

}
