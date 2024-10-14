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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Nome do produto obrigatório")
    private String nomeProduto;
    @NotNull(message = "Preço obrigatório")
    private BigDecimal preco;
    @NotNull(message = "Data da compra obrigatória")
    private LocalDate dataCompra;
    @NotBlank(message = "Descrição obrigatória")
    private String descricao;
    @NotNull(message = "Quantidade de produtos obrigatória")
    private Integer quantidade = 0; // Valor padrão para evitar null
    private Integer quantidadeVendida = 0;
    private String statusEstoque;

    public ProdutoEntity(Integer id, String nomeProduto, BigDecimal preco, LocalDate dataCompra, String descricao, Integer quantidade) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.preco = preco;
        this.dataCompra = dataCompra;
        this.descricao = descricao;
        this.quantidade = quantidade != null ? quantidade : 0; // Garantia de valor inicial
        this.quantidadeVendida = 0;
        this.statusEstoque = calcularStatusEstoque();
    }

    public ProdutoEntity() {
        this.quantidade = 0; // Inicializando para evitar null
        this.quantidadeVendida = 0;
        this.statusEstoque = calcularStatusEstoque();
    }

    public void vender(int quantidadeVendida) {
        if (this.quantidade == null) {
            this.quantidade = 0;
        }
        if (quantidadeVendida > getQuantidadeRestante()) {
            throw new IllegalArgumentException("Estoque insuficiente para a quantidade solicitada.");
        }
        this.quantidadeVendida += quantidadeVendida;
        this.statusEstoque = calcularStatusEstoque();
    }

    public int getQuantidadeRestante() {
        if (this.quantidade == null) {
            this.quantidade = 0;
        }
        if (this.quantidadeVendida == null) {
            this.quantidadeVendida = 0;
        }
        return this.quantidade - this.quantidadeVendida;
    }

    private String calcularStatusEstoque() {
        int quantidadeRestante = getQuantidadeRestante();
        if (quantidadeRestante <= 0) {
            return "Esgotado";
        } else if (quantidadeRestante < 5) {
            return "Quase Esgotando";
        } else {
            return "Disponível";
        }
    }

    public void setQuantidadeRestante(int quantidadeRestante) {
        if (quantidadeRestante < 0) {
            throw new IllegalArgumentException("A quantidade restante não pode ser negativa.");
        }
        if (this.quantidade == null) {
            this.quantidade = 0;
        }
        this.quantidadeVendida = this.quantidade - quantidadeRestante;
        this.statusEstoque = calcularStatusEstoque();
    }
}