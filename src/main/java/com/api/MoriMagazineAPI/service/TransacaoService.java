package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.ItemTransacao;
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

    public Optional<TransacaoEntity> getTransacaoById(Long id) {
        return transacaoRepository.findById(id);
    }

    public TransacaoEntity criarTransacao(TransacaoEntity transacao) {
        // Calcula o valor total da transação com base nos itens
        BigDecimal valorTotal = calcularValorTotal(transacao);
        transacao.setValorTotal(valorTotal);

        // Cria as parcelas se a forma de pagamento for "Crediário" ou "Cartão de Crédito"
        if (transacao.getFormaPagamento().equals("Crediário") || transacao.getFormaPagamento().equals("Cartão de Crédito")) {
            transacao.criarParcelas(valorTotal, transacao.getNumeroParcelas(), transacao.getDataVencimento());
        }

        return transacaoRepository.save(transacao);
    }

    public TransacaoEntity atualizarTransacao(Long id, TransacaoEntity transacaoAtualizada) {
        return transacaoRepository.findById(id)
                .map(transacao -> {
                    // Atualiza os campos da transação (exceto itens e parcelas)
                    transacao.setCliente(transacaoAtualizada.getCliente());
                    transacao.setFormaPagamento(transacaoAtualizada.getFormaPagamento());
                    transacao.setDataTransacao(transacaoAtualizada.getDataTransacao());
                    transacao.setStatus(transacaoAtualizada.getStatus());
                    transacao.setNumeroCartao(transacaoAtualizada.getNumeroCartao());
                    transacao.setChavePix(transacaoAtualizada.getChavePix());

                    // Remove parcelas existentes se a forma de pagamento mudou para à vista
                    if (!transacaoAtualizada.getFormaPagamento().equals("Crediário")
                            && !transacaoAtualizada.getFormaPagamento().equals("Cartão de Crédito")) {
                        transacao.getParcelas().clear();
                    } else if (transacaoAtualizada.getFormaPagamento().equals("Crediário")
                            || transacaoAtualizada.getFormaPagamento().equals("Cartão de Crédito")) {

                        // Recalcula parcelas se a forma de pagamento for parcelada
                        transacao.getParcelas().clear(); // Limpa parcelas antigas
                        transacao.criarParcelas(transacao.getValorTotal(), transacaoAtualizada.getNumeroParcelas(), transacaoAtualizada.getDataVencimento());
                    }

                    // Recalcula o valor total (se necessário)
                    BigDecimal novoValorTotal = calcularValorTotal(transacao);
                    transacao.setValorTotal(novoValorTotal);

                    return transacaoRepository.save(transacao);
                })
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
    }

    public void deletarTransacao(Long id) {
        transacaoRepository.deleteById(id);
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

    public List<TransacaoEntity> listarTransacoesPorMesEAno(int mes, int ano) {
        return transacaoRepository.findByDataTransacaoMonthAndYear(mes, ano);
    }

    // Método auxiliar para calcular o valor total da transação
    private BigDecimal calcularValorTotal(TransacaoEntity transacao) {
        return transacao.getItens().stream()
                .map(ItemTransacao::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
 
    }
public void carregarProdutosParaTransacoes(List<TransacaoEntity> transacoes) {
    for (TransacaoEntity transacao : transacoes) {
        List<ProdutoEntity> produtos = produtoService.listarProdutosPorIds(transacao.getProdutosIds());
        transacao.setProdutos(produtos);
    }
}

}
