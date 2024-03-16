package com.api.MoriMagazineAPI.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.util.Date;

@Data
@Entity
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Nome do produto obrigatório")
    private String nomeProduto;

    @NotNull(message = "Preço obrigatório")
    private double preco;

    private Date compra;

    // Construtor, getters e setters

    public ProdutoEntity() {
        // Construtor vazio necessário para JPA
    }

    // Construtor com parâmetros
    public ProdutoEntity(Integer id, String nomeProduto, double preco, Date compra) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.preco = preco;
        this.compra = compra;
    }
}
