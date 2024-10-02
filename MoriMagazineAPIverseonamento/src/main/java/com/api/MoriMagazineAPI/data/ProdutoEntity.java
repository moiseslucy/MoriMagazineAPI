package com.api.MoriMagazineAPI.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "Produto")
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Correção: deve ser GenerationType.IDENTITY para auto-incremento
    private Long id;  // ID agora é do tipo Long

    @NotBlank(message = "Nome do produto obrigatório")
    private String nomeProduto;

    @NotNull(message = "Preço obrigatório")
    private BigDecimal preco;

    @NotNull(message = "Data da compra obrigatória")
    private LocalDate dataCompra;

    @NotBlank(message = "Descrição obrigatória")
    private String descricao;

    @NotNull(message = "Quantidade de produtos obrigatória")
    private Integer quantidade;

    // Construtor com parâmetros
    public ProdutoEntity(Long id, String nomeProduto, BigDecimal preco, LocalDate dataCompra, String descricao, Integer quantidade) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.preco = preco;
        this.dataCompra = dataCompra;
        this.descricao = descricao;
        this.quantidade = quantidade;
    }

    // Construtor padrão (necessário para JPA)
    public ProdutoEntity() {
    }
}
