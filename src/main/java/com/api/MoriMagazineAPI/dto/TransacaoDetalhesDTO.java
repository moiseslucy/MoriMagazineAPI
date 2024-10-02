package com.api.MoriMagazineAPI.dto;

import com.api.MoriMagazineAPI.data.ItemTransacao;
import com.api.MoriMagazineAPI.data.ParcelaEntity;
import com.api.MoriMagazineAPI.data.TransacaoEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class TransacaoDetalhesDTO {
    private String clienteNome;
    private Long clienteId;
    private List<ProdutoDetalhesDTO> produtos;
    private BigDecimal subtotal;
    private String formaPagamento;
    private BigDecimal total;
    private LocalDate dataCompra;
    private List<ParcelaDetalhesDTO> parcelas;

    public TransacaoDetalhesDTO(TransacaoEntity transacao) {
        this.clienteNome = transacao.getCliente().getNome();
        this.clienteId = transacao.getCliente().getId();
        this.produtos = transacao.getItens().stream()
                .map(ProdutoDetalhesDTO::new)
                .collect(Collectors.toList());
        this.subtotal = transacao.getValorTotal();
        this.formaPagamento = transacao.getFormaPagamento();
        this.total = transacao.getValorTotal();
        this.dataCompra = transacao.getDataTransacao();
        this.parcelas = transacao.getParcelas().stream()
                .map(ParcelaDetalhesDTO::new)
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    public static class ProdutoDetalhesDTO {
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

    @Data
    @NoArgsConstructor
    public static class ParcelaDetalhesDTO {
        private LocalDate dataVencimento;
        private String status;
        private BigDecimal valorParcela;

        public ParcelaDetalhesDTO(ParcelaEntity parcela) {
            this.dataVencimento = parcela.getDataVencimento();
            this.status = parcela.getStatus().name();
            this.valorParcela = parcela.getValorParcela();
        }
    }
}
