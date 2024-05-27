package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.data.TransacaoEntity;
import com.api.MoriMagazineAPI.data.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ProdutoService produtoService;

    @Autowired
    public TransacaoService(TransacaoRepository transacaoRepository, ProdutoService produtoService) {
        this.transacaoRepository = transacaoRepository;
        this.produtoService = produtoService;
    }

    public List<TransacaoEntity> listarTodasTransacoes() {
        return transacaoRepository.findAll();
    }

    public TransacaoEntity criarTransacao(TransacaoEntity transacao) {
        BigDecimal valorTotal = calcularValorTotal(transacao);
        transacao.setValorTotal(valorTotal);
        return transacaoRepository.save(transacao);
    }

    public TransacaoEntity atualizarTransacao(Long id, TransacaoEntity transacaoAtualizada) {
        return transacaoRepository.findById(id)
                .map(transacao -> {
                    transacao.setCliente(transacaoAtualizada.getCliente());
                    transacao.setFormaPagamento(transacaoAtualizada.getFormaPagamento());
                    transacao.setDataTransacao(transacaoAtualizada.getDataTransacao());
                    transacao.setStatus(transacaoAtualizada.getStatus());
                    transacao.setNumeroCartao(transacaoAtualizada.getNumeroCartao());
                    transacao.setNumeroParcelas(transacaoAtualizada.getNumeroParcelas());
                    transacao.setValorPago(transacaoAtualizada.getValorPago());
                    transacao.setDataPagamento(transacaoAtualizada.getDataPagamento());
                    transacao.setValorParcela(transacaoAtualizada.getValorParcela());
                    transacao.setDataVencimento(transacaoAtualizada.getDataVencimento());
                    transacao.setChavePix(transacaoAtualizada.getChavePix());

                    // Recalcula o valor total
                    BigDecimal novoValorTotal = calcularValorTotal(transacao);
                    transacao.setValorTotal(novoValorTotal);

                    return transacaoRepository.save(transacao);
                })
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
    }

    public void deletarTransacao(Long id) {
        transacaoRepository.deleteById(id);
    }

    public Optional<TransacaoEntity> getTransacaoById(Long id) {
        return transacaoRepository.findById(id);
    }

    public List<TransacaoEntity> listarTransacoesPorCliente(Long clienteId) {
        return transacaoRepository.findByClienteId(clienteId);
    }

    public List<TransacaoEntity> listarTransacoesPorProduto(ProdutoEntity produto) {
        return transacaoRepository.findByProdutosIdsContaining(produto.getId());
    }

    public List<TransacaoEntity> listarTransacoesPorMes(int mes) {
        return transacaoRepository.findByDataTransacaoMonth(mes);
    }

    public List<TransacaoEntity> listarTransacoesPorFormaPagamento(String formaPagamento) {
        return transacaoRepository.findByFormaPagamento(formaPagamento);
    }

    // Método auxiliar para calcular o valor total da transação
    private BigDecimal calcularValorTotal(TransacaoEntity transacao) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Long produtoId : transacao.getProdutosIds()) {
            ProdutoEntity produto = produtoService.getProdutoId(produtoId);
            if (produto != null) {
                valorTotal = valorTotal.add(produto.getPreco().multiply(new BigDecimal(produto.getQuantidade())));
            }
        }
        return valorTotal;
    }
}
