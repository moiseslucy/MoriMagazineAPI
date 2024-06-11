package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ProdutoService produtoService;
    private final ClienteRepository clienteRepository;

    @Autowired
    public TransacaoService(TransacaoRepository transacaoRepository, ProdutoService produtoService, ClienteRepository clienteRepository) {
        this.transacaoRepository = transacaoRepository;
        this.produtoService = produtoService;
        this.clienteRepository = clienteRepository;
    }

    public List<TransacaoEntity> listarTodasTransacoes() {
        return transacaoRepository.findAll();
    }

    public Optional<TransacaoEntity> getTransacaoById(Long id) {
        return transacaoRepository.findById(id);
    }
    
    public TransacaoEntity criarTransacao(TransacaoEntity transacao, Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("O ID do cliente não pode ser nulo");
        }

        ClienteEntity cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + clienteId));
        transacao.setCliente(cliente);

        // Associa os produtos à transação
        List<ProdutoEntity> produtos = transacao.getProdutosIds().stream()
                .map(produtoService::getProdutoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        transacao.setProdutos(produtos);

        // Calcula o valor total da transação
        transacao.setValorTotal(calcularValorTotal(transacao));

        // Verifique o número de parcelas antes de criar parcelas
        if (transacao.getNumeroParcelas() == null) {
            transacao.setNumeroParcelas(1); // Valor padrão se for null
        } else if (transacao.getNumeroParcelas() <= 0) {
            throw new IllegalArgumentException("O número de parcelas deve ser maior que zero");
        }

        // Verifique a data de vencimento antes de criar parcelas
        if ("Crediário".equals(transacao.getFormaPagamento()) || "Cartão de Crédito".equals(transacao.getFormaPagamento())) {
            if (transacao.getDataVencimento() == null) {
                throw new IllegalArgumentException("A data de vencimento não pode ser nula");
            }
            transacao.criarParcelas(transacao.getValorTotal(), transacao.getNumeroParcelas(), transacao.getDataVencimento());
        }

        return transacaoRepository.save(transacao);
    }

    public TransacaoEntity atualizarTransacao(Long id, TransacaoEntity transacaoAtualizada) {
        return transacaoRepository.findById(id)
                .map(transacao -> {
                    // Atualiza os campos da transação
                    transacao.setCliente(transacaoAtualizada.getCliente());
                    transacao.setFormaPagamento(transacaoAtualizada.getFormaPagamento());
                    transacao.setDataTransacao(transacaoAtualizada.getDataTransacao());
                    transacao.setStatus(transacaoAtualizada.getStatus());
                    transacao.setNumeroCartao(transacaoAtualizada.getNumeroCartao());
                    transacao.setChavePix(transacaoAtualizada.getChavePix());

                    // Atualiza os produtos e recalcula o valor total
                    List<ProdutoEntity> produtos = produtoService.listarProdutosPorIds(transacaoAtualizada.getProdutosIds());
                    transacao.setProdutos(produtos);
                    BigDecimal novoValorTotal = calcularValorTotal(transacao);
                    transacao.setValorTotal(novoValorTotal);

                    // Recalcula parcelas se a forma de pagamento for parcelada
                    if ("Crediário".equals(transacaoAtualizada.getFormaPagamento()) || "Cartão de Crédito".equals(transacaoAtualizada.getFormaPagamento())) {
                        transacao.getParcelas().clear();
                        transacao.criarParcelas(novoValorTotal, transacaoAtualizada.getNumeroParcelas(), transacaoAtualizada.getDataVencimento());
                    } else {
                        transacao.getParcelas().clear();
                    }

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

    public List<TransacaoEntity> listarTransacoesPorMesAno(int mes, int ano) {
        List<TransacaoEntity> transacoes = transacaoRepository.findAll();
        return transacoes.stream()
                .filter(transacao -> transacao.getDataTransacao().getMonthValue() == mes && transacao.getDataTransacao().getYear() == ano)
                .collect(Collectors.toList());
    }

    // Método auxiliar para calcular o valor total da transação
    private BigDecimal calcularValorTotal(TransacaoEntity transacao) {
        return transacao.getItens().stream()
                .map(ItemTransacao::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
