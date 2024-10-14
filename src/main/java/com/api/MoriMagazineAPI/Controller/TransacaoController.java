







package com.api.MoriMagazineAPI.controller;
/*
import com.api.MoriMagazineAPI.data.ClienteEntity;
import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.data.TransacaoEntity;
import com.api.MoriMagazineAPI.service.ClienteService;
import com.api.MoriMagazineAPI.service.ProdutoService;
import com.api.MoriMagazineAPI.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transacao")
public class TransacaoController {

    private final TransacaoService transacaoService;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    @Autowired
    public TransacaoController(TransacaoService transacaoService, ClienteService clienteService, ProdutoService produtoService) {
        this.transacaoService = transacaoService;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    @GetMapping("/listar")
    public String viewTransacoesPage(Model model) {
        List<TransacaoEntity> transacoes = transacaoService.listarTodasTransacoes();
        
        // Carregar os produtos associados a cada transação
        for (TransacaoEntity transacao : transacoes) {
            List<Long> produtosIds = transacao.getProdutosIds();
            List<ProdutoEntity> produtos = new ArrayList<>();
            for (Long produtoId : produtosIds) {
                ProdutoEntity produto = produtoService.buscarProdutoPorId(produtoId);
                produtos.add(produto);
            }
            transacao.setProdutos(produtos);
        }

        model.addAttribute("transacoes", transacoes);
        return "indexTransacoes";
    }

    @GetMapping("/criarForm")
    public String criarTransacaoForm(Model model) {
        model.addAttribute("clientes", clienteService.listarTodosClientes());
        model.addAttribute("produtos", produtoService.listarTodosProdutos());
        model.addAttribute("transacao", new TransacaoEntity());
        return "inserirTransacao";
    }

    @PostMapping("/salvarTransacao")
    public String salvarTransacao(@Valid @ModelAttribute("transacao") TransacaoEntity transacao,
                                  BindingResult result,
                                  @RequestParam(value = "produtosSelecionados", required = false) List<Long> produtosSelecionados,
                                  @RequestParam(value = "numeroParcelas", required = false) Integer numeroParcelas,
                                  @RequestParam(value = "dataVencimento", required = false) String dataVencimento,
                                  @RequestParam(value = "idCliente") Long idCliente,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (result.hasErrors() || produtosSelecionados == null || produtosSelecionados.isEmpty()) {
            if (produtosSelecionados == null || produtosSelecionados.isEmpty()) {
                result.rejectValue("produtosIds", "error.transacao.produtos.vazio", "Selecione pelo menos um produto");
            }
            model.addAttribute("clientes", clienteService.listarTodosClientes());
            model.addAttribute("produtos", produtoService.listarTodosProdutos());
            return "inserirTransacao";
        }

        // Carregar os detalhes dos produtos associados à transação
        List<ProdutoEntity> produtos = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Long produtoId : produtosSelecionados) {
            ProdutoEntity produto = produtoService.buscarProdutoPorId(produtoId);
            if (produto == null) {
                result.rejectValue("produtosIds", "error.transacao.produtoNaoEncontrado", "Produto não encontrado");
                model.addAttribute("clientes", clienteService.listarTodosClientes());
                model.addAttribute("produtos", produtoService.listarTodosProdutos());
                return "inserirTransacao";
            }
            produtos.add(produto);
            valorTotal = valorTotal.add(produto.getPreco());
        }
        transacao.setProdutos(produtos);
        transacao.setValorTotal(valorTotal);

        if (numeroParcelas != null && dataVencimento != null) {
            LocalDate dataVenc = LocalDate.parse(dataVencimento);
            transacao.setNumeroParcelas(numeroParcelas);
            transacao.setDataVencimento(dataVenc);
            transacao.criarParcelas(valorTotal, numeroParcelas, dataVenc);
        }

        transacaoService.criarTransacao(transacao, idCliente);
        redirectAttributes.addFlashAttribute("successMessage", "Transação registrada com sucesso!");
        return "redirect:/transacao/listar";
    }

    @GetMapping("/atualizarForm/{id}")
    public String atualizarTransacaoForm(@PathVariable Long id, Model model) {
        return transacaoService.getTransacaoById(id)
                .map(transacao -> {
                    model.addAttribute("transacao", transacao);
                    List<ProdutoEntity> produtosTransacao = transacao.getProdutosIds().stream()
                            .map(produtoService::buscarProdutoPorId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    List<ProdutoEntity> todosProdutos = produtoService.listarTodosProdutos();
                    model.addAttribute("clientes", clienteService.listarTodosClientes());
                    model.addAttribute("produtosTransacao", produtosTransacao);
                    model.addAttribute("todosProdutos", todosProdutos);
                    return "atualizarTransacao";
                })
                .orElse("redirect:/transacao/listar");
    }

    @PostMapping("/atualizar")
    public String atualizarTransacao(@Valid @ModelAttribute("transacao") TransacaoEntity transacao,
                                     BindingResult result,
                                     @RequestParam(value = "produtosSelecionados", required = false) List<Long> produtosSelecionados,
                                     @RequestParam(value = "numeroParcelas", required = false) Integer numeroParcelas,
                                     @RequestParam(value = "dataVencimento", required = false) String dataVencimento,
                                     Model model) {
        if (result.hasErrors() || (produtosSelecionados == null || produtosSelecionados.isEmpty())) {
            if (produtosSelecionados == null || produtosSelecionados.isEmpty()) {
                result.rejectValue("produtosIds", "error.transacao.produtos.vazio", "Selecione pelo menos um produto");
            }
            model.addAttribute("clientes", clienteService.listarTodosClientes());
            model.addAttribute("produtos", produtoService.listarTodosProdutos());
            return "atualizarTransacao";
        }
        transacao.setProdutosIds(produtosSelecionados);
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Long produtoId : transacao.getProdutosIds()) {
            ProdutoEntity produto = produtoService.buscarProdutoPorId(produtoId);
            valorTotal = valorTotal.add(produto.getPreco());
        }
        transacao.setValorTotal(valorTotal);

        if (numeroParcelas != null && dataVencimento != null) {
            LocalDate dataVenc = LocalDate.parse(dataVencimento);
            transacao.setNumeroParcelas(numeroParcelas);
            transacao.setDataVencimento(dataVenc);
            transacao.criarParcelas(valorTotal, numeroParcelas, dataVenc);
        }
        transacaoService.atualizarTransacao(transacao.getId(), transacao);
        return "redirect:/transacao/listar";
    }

    @GetMapping("/deletar/{id}")
    public String deletarTransacao(@PathVariable Long id) {
        transacaoService.deletarTransacao(id);
        return "redirect:/transacao/listar";
    }

    @GetMapping("/buscar-por-cliente/{clienteId}")
    public String buscarTransacoesPorCliente(@PathVariable Long clienteId, Model model) {
        List<TransacaoEntity> transacoes = transacaoService.listarTransacoesPorCliente(clienteId);
        model.addAttribute("transacoes", transacoes);
        return "indexTransacoes";
    }

    @GetMapping("/buscar-por-id-produto/{produtoId}")
    public String buscarTransacoesPorProdutoId(@PathVariable Long produtoId, Model model) {
        ProdutoEntity produto = produtoService.buscarProdutoPorId(produtoId);
        if (produto != null) {
            List<TransacaoEntity> transacoes = transacaoService.listarTransacoesPorProduto(produto);
            model.addAttribute("transacoes", transacoes);
            model.addAttribute("produto", produto);
            return "indexTransacoes";
        } else {
            return "redirect:/transacao/listar";
        }
    }

    @GetMapping("/buscar-por-mes")
    public String buscarTransacoesPorMes(@RequestParam("mes") int mes, Model model) {
        List<TransacaoEntity> transacoes = transacaoService.listarTransacoesPorMes(mes);
        model.addAttribute("transacoes", transacoes);
        return "indexTransacoes";
    }

    @GetMapping("/buscar-por-mes-e-ano")
    public String buscarTransacoesPorMesEAno(@RequestParam("mes") int mes, @RequestParam("ano") int ano, Model model) {
        List<TransacaoEntity> transacoes = transacaoService.listarTransacoesPorMesAno(mes, ano);
        model.addAttribute("transacoes", transacoes);
        return "indexTransacoes";
    }
}*/