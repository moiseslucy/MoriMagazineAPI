package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.service.ProdutoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produto")
public class ProdutoControllerWeb {

    @Autowired
    ProdutoService produtoService;

    @GetMapping("/listar")
    public String viewProdutosPage(Model model) {
        model.addAttribute("listarProdutos", produtoService.listarTodosProdutos());
        return "IndexProdutos";
    }

    @GetMapping("/deletar/{id}")
    public String mostrarFormularioExclusao(@PathVariable(value = "id") Integer id, Model model) {
        ProdutoEntity produto = produtoService.getProdutoId(id); // Ajustado para o método correto
        if (produto == null) {
            return "redirect:/produto/listar";
        }
        model.addAttribute("produto", produto);
        return "ConfirmarExclusaoProduto";
    }

    @PostMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable(value = "id") Integer id) {
        produtoService.deletarProduto(id);
        return "redirect:/produto/listar";
    }

    @GetMapping("/criarForm")
    public String criarProdutoForm(Model model) {
        model.addAttribute("produto", new ProdutoEntity());
        return "inserirProduto";
    }

    @PostMapping("/salvarProduto")
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoEntity produto, BindingResult result) {
        if (result.hasErrors()) {
            return "inserirProduto";
        }
        try {
            String precoFormatado = produto.getPreco().toString().replaceAll("[^\\d.,]", "");
            precoFormatado = precoFormatado.replace(",", ".");
            BigDecimal precoDecimal = new BigDecimal(precoFormatado);
            produto.setPreco(precoDecimal);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (produto.getId() == null) {
            produtoService.criarProduto(produto);
        } else {
            produtoService.atualizarProduto(produto.getId(), produto); // Ajustado para passar o ID
        }
        return "redirect:/produto/listar";
    }

    @GetMapping("/pesquisar")
    public String pesquisarProdutoPorNome(@RequestParam("termo") String termo, Model model) {
        List<ProdutoEntity> resultados = produtoService.getProdutoPorNome(termo);
        model.addAttribute("resultados", resultados);
        return "pesquisa-produto";
    }

    @GetMapping("/atualizarForm/{id}")
    public String mostrarFormularioAtualizacao(@PathVariable(value = "id") Integer id, Model model) {
        ProdutoEntity produto = produtoService.getProdutoId(id); // Ajustado para o método correto
        model.addAttribute("produto", produto);
        return "atualizarProduto";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarProduto(@PathVariable(value = "id") Integer id, @Valid @ModelAttribute("produto") ProdutoEntity produto, BindingResult result) {
        if (result.hasErrors()) {
            return "atualizarProduto";
        }
        try {
            String precoFormatado = produto.getPreco().toString().replaceAll("[^\\d.,]", "");
            precoFormatado = precoFormatado.replace(",", ".");
            BigDecimal precoDecimal = new BigDecimal(precoFormatado);
            produto.setPreco(precoDecimal);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        produtoService.atualizarProduto(id, produto); // Ajustado para passar o ID
        return "redirect:/produto/listar";
    }

    @GetMapping("/buscarPorId/{id}")
    public String buscarProdutoPorId(@PathVariable("id") Integer id, Model model) {
        ProdutoEntity produto = produtoService.getProdutoId(id); // Ajustado para o método correto
        if (produto == null) {
            return "redirect:/produto/listar";
        }
        model.addAttribute("produto", produto);
        return "detalhesProduto";
    }
}