package com.api.MoriMagazineAPI.controller;

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
                              RedirectAttributes redirectAttributes, // Adiciona RedirectAttributes
                              Model model) {
    if (result.hasErrors() || produtosSelecionados == null || produtosSelecionados.isEmpty()) {
        // Trata erros de validação e de produtos não selecionados
        if (produtosSelecionados == null || produtosSelecionados.isEmpty()) {
            result.rejectValue("produtosIds", "error.transacao.produtos.vazio", "Selecione pelo menos um produto");
        }
        model.addAttribute("clientes", clienteService.listarTodosClientes());
        model.addAttribute("produtos", produtoService.listarTodosProdutos());
        return "inserirTransacao";
    }

    transacao.setProdutosIds(produtosSelecionados);

    BigDecimal valorTotal = BigDecimal.ZERO;
    for (Long produtoId : transacao.getProdutosIds()) {
        ProdutoEntity produto = produtoService.getProdutoId(produtoId);
        if (produto == null) {
            // Trata o caso de produto não encontrado
            result.rejectValue("produtosIds", "error.transacao.produtoNaoEncontrado", "Produto não encontrado");
            model.addAttribute("clientes", clienteService.listarTodosClientes());
            model.addAttribute("produtos", produtoService.listarTodosProdutos());
            return "inserirTransacao";
        }
        valorTotal = valorTotal.add(produto.getPreco().multiply(new BigDecimal(produto.getQuantidade())));
    }
    transacao.setValorTotal(valorTotal);

    transacaoService.criarTransacao(transacao);

    // Adiciona uma mensagem de sucesso para ser exibida na página de listagem
    redirectAttributes.addFlashAttribute("successMessage", "Transação registrada com sucesso!");
    return "redirect:/transacao/listar"; // Redireciona para a página de listagem
}


    @GetMapping("/atualizarForm/{id}")
    public String atualizarTransacaoForm(@PathVariable Long id, Model model) {
        return transacaoService.getTransacaoById(id)
                .map(transacao -> {
                    model.addAttribute("transacao", transacao);

                    // Busca os produtos da transação e os produtos disponíveis
                    List<ProdutoEntity> produtosTransacao = transacao.getProdutosIds().stream()
                            .map(produtoService::getProdutoId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    List<ProdutoEntity> todosProdutos = produtoService.listarTodosProdutos();

                    model.addAttribute("clientes", clienteService.listarTodosClientes());
                    model.addAttribute("produtosTransacao", produtosTransacao);
                    model.addAttribute("todosProdutos", todosProdutos);

                    return "atualizarTransacao";
                })
                .orElse("redirect:/transacao/listar"); // Redireciona se não encontrar
    }
@PostMapping("/atualizar")
public String atualizarTransacao(@Valid @ModelAttribute("transacao") TransacaoEntity transacao,
                                 BindingResult result,
                                 @RequestParam(value = "produtosSelecionados", required = false) List<Long> produtosSelecionados,
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

    // Calcula o valor total da transação
    BigDecimal valorTotal = BigDecimal.ZERO;
    for (Long produtoId : transacao.getProdutosIds()) {
        ProdutoEntity produto = produtoService.getProdutoId(produtoId);
        valorTotal = valorTotal.add(produto.getPreco().multiply(new BigDecimal(produto.getQuantidade())));
    }
    transacao.setValorTotal(valorTotal);

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
        ProdutoEntity produto = produtoService.getProdutoId(produtoId);
        if (produto != null) {
            List<TransacaoEntity> transacoes = transacaoService.listarTransacoesPorProduto(produto);
            model.addAttribute("transacoes", transacoes);
            model.addAttribute("produto", produto);
            return "indexTransacoes";
        } else {
            return "redirect:/transacao/listar";
        }
    }

    @GetMapping("/filtrar-por-mes")
    public String filtrarTransacoesPorMes(@RequestParam("mes") int mes, Model model) {
        List<TransacaoEntity> transacoesDoMes = transacaoService.listarTransacoesPorMes(mes);
        model.addAttribute("transacoes", transacoesDoMes);
        return "indexTransacoes";
    }

    @GetMapping("/buscar-por-forma-pagamento/{formaPagamento}")
    public String buscarTransacoesPorFormaPagamento(@PathVariable String formaPagamento, Model model) {
        List<TransacaoEntity> transacoes = transacaoService.listarTransacoesPorFormaPagamento(formaPagamento);
        model.addAttribute("transacoes", transacoes);
        return "indexTransacoes";
    }
}

