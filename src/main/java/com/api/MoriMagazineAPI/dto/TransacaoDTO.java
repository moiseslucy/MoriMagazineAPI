package com.api.MoriMagazineAPI.dto;

import com.api.MoriMagazineAPI.data.ItemTransacao;
import com.api.MoriMagazineAPI.data.ParcelaEntity;
import com.api.MoriMagazineAPI.data.TransacaoEntity;
import com.api.MoriMagazineAPI.service.ProdutoService;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TransacaoDTO {
    
    @NotNull
    private Integer clienteId; // Alterado para Integer
    @NotNull
    private List<ItemDTO> itens;
    @NotNull
    private String status;
    @NotNull
    private LocalDate dataTransacao;
    @NotNull
    private String formaPagamento;
    private List<ParcelaDTO> parcelas;
    private LocalDate dataVencimento;
    private Integer numeroParcelas;

    // Método para converter para entidade
    public TransacaoEntity toTransacaoEntity(ProdutoService produtoService) {
        TransacaoEntity transacaoEntity = new TransacaoEntity();
        transacaoEntity.setClienteId(this.clienteId); // Utilizando Integer para clienteId
        transacaoEntity.setStatus(this.status);
        transacaoEntity.setDataTransacao(this.dataTransacao);
        transacaoEntity.setFormaPagamento(this.formaPagamento);
        transacaoEntity.setDataVencimento(this.dataVencimento); 
        transacaoEntity.setNumeroParcelas(this.numeroParcelas);

        // Converte os itens de ItemDTO para ItemTransacao e adiciona na entidade
        List<ItemTransacao> itensTransacao = this.itens.stream().map(itemDTO -> {
            ItemTransacao itemTransacao = new ItemTransacao();
            itemTransacao.setProduto(produtoService.getProdutoId(itemDTO.getProdutoId()));
            itemTransacao.setQuantidade(itemDTO.getQuantidade());
            itemTransacao.setPrecoUnitario(itemTransacao.getProduto().getPreco());
            itemTransacao.setSubtotal(itemTransacao.getPrecoUnitario().multiply(new BigDecimal(itemDTO.getQuantidade())));
            itemTransacao.setTransacao(transacaoEntity); 
            return itemTransacao;
        }).collect(Collectors.toList());

        transacaoEntity.setItens(itensTransacao);

        // Configura as parcelas apenas para pagamentos a prazo
        if ("Crediário".equals(this.formaPagamento) || "Cartão de Crédito".equals(this.formaPagamento)) {
            List<ParcelaEntity> parcelasEntity = this.parcelas.stream().map(parcelaDTO -> {
                ParcelaEntity parcelaEntity = new ParcelaEntity();
                parcelaEntity.setTransacao(transacaoEntity);
                parcelaEntity.setValorParcela(parcelaDTO.getValorParcela());
                parcelaEntity.setDataVencimento(parcelaDTO.getDataVencimento());
                parcelaEntity.setStatus(parcelaDTO.getStatus());
                return parcelaEntity;
            }).collect(Collectors.toList());
            transacaoEntity.setParcelas(parcelasEntity);
        } else {
            transacaoEntity.setParcelas(null); // Sem parcelas para pagamentos à vista
        }

        return transacaoEntity;
    }

    public Integer getClienteId() {
        return clienteId;
    }
}
