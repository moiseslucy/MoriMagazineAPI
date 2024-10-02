package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoServicePostman {

    private static final Logger logger = LoggerFactory.getLogger(TransacaoServicePostman.class);

    private final TransacaoRepository transacaoRepository;
    private final ProdutoService produtoService;
    private final ClienteRepository clienteRepository;
    private final ParcelaRepository parcelaRepository;

    @Autowired
    public TransacaoServicePostman(TransacaoRepository transacaoRepository, ProdutoService produtoService, ClienteRepository clienteRepository, ParcelaRepository parcelaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.produtoService = produtoService;
        this.clienteRepository = clienteRepository;
        this.parcelaRepository = parcelaRepository;
    }

    public List<TransacaoEntity> listarTodasTransacoes() {
        logger.debug("Listando todas as transações");
        List<TransacaoEntity> transacoes = transacaoRepository.findAll();
        transacoes.forEach(transacao -> logger.debug("Transação: {}", transacao));
        return transacoes;
    }

    public Optional<TransacaoEntity> getTransacaoById(Long id) {
        logger.debug("Buscando transação com ID: {}", id);
        return transacaoRepository.findById(id);
    }

    @Transactional
    public TransacaoEntity criarTransacao(TransacaoEntity transacao, Long clienteId) {
        logger.debug("Criando transação sem quantidades especificadas para o cliente ID: {}", clienteId);
        validarCliente(clienteId);
        associarClienteATransacao(transacao, clienteId);
        calcularValorTotalEParcelas(transacao);

        TransacaoEntity transacaoSalva = transacaoRepository.save(transacao);
        logger.debug("Transação salva: {}", transacaoSalva);
        return transacaoSalva;
    }

    public TransacaoEntity atualizarTransacao(Long id, TransacaoEntity transacaoAtualizada) {
        logger.debug("Atualizando transação com ID: {}", id);
        return transacaoRepository.findById(id)
                .map(transacao -> {
                    atualizarDetalhesTransacao(transacao, transacaoAtualizada);
                    calcularValorTotalEParcelas(transacao);
                    TransacaoEntity transacaoAtualizadaSalva = transacaoRepository.save(transacao);
                    logger.debug("Transação atualizada salva: {}", transacaoAtualizadaSalva);
                    return transacaoAtualizadaSalva;
                })
                .orElseThrow(() -> {
                    logger.error("Transação não encontrada com ID: {}", id);
                    return new RuntimeException("Transação não encontrada");
                });
    }

    public void deletarTransacao(Long id) {
        logger.debug("Deletando transação com ID: {}", id);
        transacaoRepository.deleteById(id);
    }

    private void validarCliente(Long clienteId) {
        logger.debug("Validando cliente com ID: {}", clienteId);
        if (clienteId == null) {
            logger.error("ID do cliente é nulo");
            throw new IllegalArgumentException("O ID do cliente não pode ser nulo");
        }

        if (!clienteRepository.existsById(clienteId)) {
            logger.error("Cliente não encontrado com o ID: {}", clienteId);
            throw new IllegalArgumentException("Cliente não encontrado com o ID: " + clienteId);
        }
    }

    private void associarClienteATransacao(TransacaoEntity transacao, Long clienteId) {
        logger.debug("Associando cliente com ID: {} à transação", clienteId);
        ClienteEntity cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> {
                    logger.error("Cliente não encontrado com o ID: {}", clienteId);
                    return new IllegalArgumentException("Cliente não encontrado com o ID: " + clienteId);
                });
        transacao.setCliente(cliente);
    }

    private void calcularValorTotalEParcelas(TransacaoEntity transacao) {
    logger.debug("Calculando valor total e parcelas da transação");
    BigDecimal valorTotal = BigDecimal.ZERO;
    for (ItemTransacao item : transacao.getItens()) {
        ProdutoEntity produto = produtoService.getProdutoId(item.getProduto().getId());
        item.setProduto(produto);
        item.setPrecoUnitario(produto.getPreco());
        item.setSubtotal(produto.getPreco().multiply(new BigDecimal(item.getQuantidade())));
        valorTotal = valorTotal.add(item.getSubtotal());
        logger.debug("Item processado: {}", item);
    }
    transacao.setValorTotal(valorTotal);
    logger.debug("Valor total calculado: {}", valorTotal);

    if ("Crediário".equals(transacao.getFormaPagamento()) || "Cartão de Crédito".equals(transacao.getFormaPagamento())) {
        if (transacao.getNumeroParcelas() == null || transacao.getNumeroParcelas() <= 0) {
            logger.error("Número de parcelas inválido ou nulo para forma de pagamento {}", transacao.getFormaPagamento());
            throw new IllegalArgumentException("O número de parcelas deve ser maior que zero para pagamentos parcelados");
        }

        if (transacao.getDataVencimento() == null) {
            logger.error("Data de vencimento é nula para forma de pagamento {}", transacao.getFormaPagamento());
            throw new IllegalArgumentException("A data de vencimento não pode ser nula para pagamentos parcelados");
        }

        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(transacao.getNumeroParcelas()), RoundingMode.HALF_UP);
        List<ParcelaEntity> parcelas = new ArrayList<>();
        for (int i = 0; i < transacao.getNumeroParcelas(); i++) {
            ParcelaEntity parcela = new ParcelaEntity(transacao, valorParcela, transacao.getDataVencimento().plusMonths(i), StatusParcela.PENDENTE);
            parcelas.add(parcela);
            parcelaRepository.save(parcela); // Salvar cada parcela no repositório
        }
        transacao.setParcelas(parcelas);
    } else {
        transacao.setParcelas(null); // Sem parcelas para pagamentos à vista ou outras formas de pagamento não parceladas
    }
}


    private void atualizarDetalhesTransacao(TransacaoEntity transacao, TransacaoEntity transacaoAtualizada) {
        logger.debug("Atualizando detalhes da transação com ID: {}", transacao.getId());
        transacao.setCliente(transacaoAtualizada.getCliente());
        transacao.setFormaPagamento(transacaoAtualizada.getFormaPagamento());
        transacao.setDataTransacao(transacaoAtualizada.getDataTransacao());
        transacao.setStatus(transacaoAtualizada.getStatus());
        transacao.setNumeroCartao(transacaoAtualizada.getNumeroCartao());
        transacao.setChavePix(transacaoAtualizada.getChavePix());

        transacao.getItens().clear();
        for (ItemTransacao item : transacaoAtualizada.getItens()) {
            ProdutoEntity produto = produtoService.getProdutoId(item.getProduto().getId());
            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco());
            item.setSubtotal(produto.getPreco().multiply(new BigDecimal(item.getQuantidade())));
            transacao.getItens().add(item);
            logger.debug("Item atualizado: {}", item);
        }
        calcularValorTotalEParcelas(transacao);
    }
}
