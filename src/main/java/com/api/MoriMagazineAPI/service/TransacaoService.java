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
public class TransacaoService {

    private static final Logger logger = LoggerFactory.getLogger(TransacaoService.class);

    private final TransacaoRepository transacaoRepository;
    private final ProdutoService produtoService;
    private final ClienteRepository clienteRepository;
    private final ParcelaRepository parcelaRepository;

    @Autowired
    public TransacaoService(TransacaoRepository transacaoRepository, ProdutoService produtoService, ClienteRepository clienteRepository, ParcelaRepository parcelaRepository) {
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

    public Optional<TransacaoEntity> getTransacaoById(Integer id) {
        logger.debug("Buscando transação com ID: {}", id);
        return transacaoRepository.findById(id);
    }

    @Transactional
    public TransacaoEntity criarTransacaoWeb(TransacaoEntity transacao, Integer clienteId, List<Integer> quantidades) {
        logger.debug("Criando transação com quantidades especificadas para o cliente ID: {}", clienteId);
        validarCliente(clienteId);
        associarClienteATransacao(transacao, clienteId);
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (int i = 0; i < transacao.getItens().size(); i++) {
            ItemTransacao item = transacao.getItens().get(i);
            ProdutoEntity produto = produtoService.getProdutoId(item.getProduto().getId());

            if (produto == null) {
                throw new RuntimeException("Produto não encontrado com ID: " + item.getProduto().getId());
            }
            int quantidade = quantidades.get(i);

            if (produto.getQuantidadeRestante() < quantidade) {
                throw new RuntimeException("Estoque insuficiente para o produto " + produto.getNomeProduto() +
                        ". Disponível: " + produto.getQuantidadeRestante() + ", solicitado: " + quantidade);
            }
            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco());
            item.setQuantidade(quantidade);
            item.setSubtotal(produto.getPreco().multiply(BigDecimal.valueOf(quantidade)));
            valorTotal = valorTotal.add(item.getSubtotal());

            produto.vender(quantidade);
         
produtoService.atualizarProduto(produto.getId(), produto);
            logger.debug("Item processado: {}", item);
        }
        transacao.setValorTotal(valorTotal);
        calcularValorTotalEParcelas(transacao);
        TransacaoEntity transacaoSalva = transacaoRepository.save(transacao);
        logger.debug("Transação salva: {}", transacaoSalva);

        return transacaoSalva;
    }

    public TransacaoEntity atualizarTransacao(Integer id, TransacaoEntity transacaoAtualizada) {
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

    public void deletarTransacao(Integer id) {
        logger.debug("Deletando transação com ID: {}", id);
        transacaoRepository.deleteById(id);
    }

    public List<TransacaoEntity> listarTransacoesPorCliente(Integer clienteId) {
        logger.debug("Listando transações para o cliente com ID: {}", clienteId);
        return transacaoRepository.findByCliente_Id(clienteId);
    }

    public List<TransacaoEntity> listarTransacoesPorProduto(ProdutoEntity produto) {
        logger.debug("Listando transações para o produto com ID: {}", produto.getId());
        return transacaoRepository.findByProduto(produto);
    }

    public List<TransacaoEntity> listarTransacoesPorMes(int mes) {
        logger.debug("Listando transações para o mês: {}", mes);
        return transacaoRepository.findByDataTransacaoMonth(mes);
    }

    public List<TransacaoEntity> listarTransacoesPorFormaPagamento(String formaPagamento) {
        logger.debug("Listando transações com forma de pagamento: {}", formaPagamento);
        return transacaoRepository.findByFormaPagamento(formaPagamento);
    }

    public List<TransacaoEntity> listarTransacoesPorMesAno(int mes, int ano) {
        logger.debug("Listando transações para o mês: {} e ano: {}", mes, ano);
        return transacaoRepository.findByDataTransacaoMonthAndYear(mes, ano);
    }

    public List<ParcelaEntity> listarParcelasAtrasadas() {
        logger.debug("Listando parcelas atrasadas");
        return parcelaRepository.findAtrasadas(LocalDate.now());
    }

    @Transactional
    public void baixarParcela(Integer parcelaId) {
        logger.debug("Baixando parcela com ID: {}", parcelaId);
        ParcelaEntity parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        parcela.setStatus(StatusParcela.PAGO);
        parcelaRepository.save(parcela);

        TransacaoEntity transacao = parcela.getTransacao();
        boolean todasPagas = transacao.getParcelas().stream()
                .allMatch(p -> p.getStatus() == StatusParcela.PAGO);
        if (todasPagas) {
            transacao.setStatus("Pago");
            transacaoRepository.save(transacao);
        }
    }

    @Transactional
    public void enviarComprovantePagamento(Integer transacaoId, Integer parcelaId) {
        logger.debug("Enviando comprovante de pagamento para parcela com ID: {}", parcelaId);
        TransacaoEntity transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        ParcelaEntity parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        if (!StatusParcela.PAGO.equals(parcela.getStatus())) {
            logger.error("Tentativa de enviar comprovante para parcela não paga, ID: {}", parcelaId);
            throw new IllegalStateException("Não é possível enviar o comprovante para parcelas não baixadas.");
        }

        String mensagem = criarMensagemComprovante(transacao);
        logger.info("Comprovante enviado com sucesso para a parcela ID: {}", parcelaId);
    }

    private String criarMensagemComprovante(TransacaoEntity transacao) {
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Detalhes da Compra\n");
        mensagem.append("Data da Compra: ").append(transacao.getDataTransacao()).append("\n");
        mensagem.append("Cliente: ").append(transacao.getCliente().getNome()).append("\n\n");
        mensagem.append("Parcelas:\n");
        for (ParcelaEntity parcela : transacao.getParcelas()) {
            mensagem.append("Data de Vencimento: ").append(parcela.getDataVencimento())
                    .append(" - Valor: R$ ").append(parcela.getValorParcela())
                    .append(" - Status: ").append(parcela.getStatus()).append("\n");
        }
        mensagem.append("\nObrigado pela compra! Se houver alguma dúvida, entre em contato.");
        return mensagem.toString();
    }

    public List<TransacaoEntity> listarTransacoesPorNomeCliente(String nomeCliente) {
        logger.debug("Listando transações para o cliente com nome: {}", nomeCliente);
        return transacaoRepository.findByClienteNome(nomeCliente);
    }

    private void validarCliente(Integer clienteId) {
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

    private void associarClienteATransacao(TransacaoEntity transacao, Integer clienteId) {
        logger.debug("Associando cliente com ID: {} à transação", clienteId);
        ClienteEntity cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> {
                    logger.error("Cliente não encontrado com o ID: {}", clienteId);
                    return new IllegalArgumentException("Cliente não encontrado com o ID: " + clienteId);
                });
        transacao.setCliente(cliente);
    }

    private void ajustarItensTransacao(TransacaoEntity transacao, List<Integer> quantidades) {
        logger.debug("Ajustando itens da transação");
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (int i = 0; i < transacao.getItens().size(); i++) {
            ItemTransacao item = transacao.getItens().get(i);
            ProdutoEntity produto = produtoService.getProdutoId(item.getProduto().getId());
            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco());
            item.setQuantidade(quantidades.get(i));
            item.setSubtotal(produto.getPreco().multiply(new BigDecimal(item.getQuantidade())));
            valorTotal = valorTotal.add(item.getSubtotal());
            logger.debug("Item ajustado: {}", item);
        }
        transacao.setValorTotal(valorTotal);
        logger.debug("Valor total da transação ajustado para: {}", valorTotal);
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
            }
            transacao.setParcelas(parcelas);
        } else {
            transacao.setParcelas(null);
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

    public List<TransacaoEntity> listarTransacoesPorMesAnoFormaPagamento(int mes, int ano, String formaPagamento) {
        logger.debug("Listando transações para o mês: {}, ano: {} e forma de pagamento: {}", mes, ano, formaPagamento);
        return transacaoRepository.findByDataTransacaoMonthAndYearAndFormaPagamento(mes, ano, formaPagamento);
    }
}
