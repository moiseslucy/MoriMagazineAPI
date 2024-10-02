package com.api.MoriMagazineAPI.dto;

import com.api.MoriMagazineAPI.data.ItemTransacao;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProdutoDetalhesDTO {
    private String nome;
    private int quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal total;

    public ProdutoDetalhesDTO(ItemTransacao item) {
        this.nome = item.getProduto().getNomeProduto();
        this.quantidade = item.getQuantidade();
        this.precoUnitario = item.getPrecoUnitario();
        this.total = item.getSubtotal();
    }
}
