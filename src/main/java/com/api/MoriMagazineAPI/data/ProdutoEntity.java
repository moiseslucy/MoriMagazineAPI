package com.api.MoriMagazineAPI.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Produto")
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Nome do produto obrigatório")
    private String nomeProduto;

    @NotBlank(message = "Preço obrigatório")
    private String preco; // Alterado para String para aceitar o preço formatado em reais (R$)

    @NotNull(message = "Data da compra obrigatória")
    private LocalDate dataCompra; // Renomeado de 'compra' para 'dataCompra'

    @NotBlank(message = "Descrição obrigatória")
    private String descricao; // Descrição do produto

    @NotNull(message = "Quantidade de produtos obrigatória")
    private Integer quantidade; // Quantidade de produtos disponíveis

    // Construtores padrão e com parâmetros, getters e setters

    public ProdutoEntity() {
        // Construtor vazio necessário para JPA
    }

    // Construtor com parâmetros
    public ProdutoEntity(Integer id, String nomeProduto, String preco, LocalDate dataCompra, String descricao, Integer quantidade) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.preco = preco;
        this.dataCompra = dataCompra;
        this.descricao = descricao;
        this.quantidade = quantidade;
    }

    // Getters e setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
