package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.service.ProdutoService;

import jakarta.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

// Importações omitidas por brevidade
@Controller
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    ProdutoService produtoService;

    @GetMapping("/listar")
    public String viewProdutosPage(Model model) {
        model.addAttribute("listarProdutos", produtoService.listarTodosProdutos());
        return "IndexProdutos";
    }

    @GetMapping("/deletar/{id}")
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
public String salvarProduto(
        @Valid @ModelAttribute("produto") ProdutoEntity produto,
        BindingResult result) {

    if (result.hasErrors()) {
        return "inserirProduto"; // Retorna para a página de inserção de produto se houver erros de validação
    }
    
    if (produto.getId() == null) {
        produtoService.criarProduto(produto);
    } else {
        produtoService.atualizarProduto(produto.getId(), produto);
    }

    return "redirect:/produto/listar"; // Redireciona para a página de listagem de produtos após salvar com sucesso
}

    @GetMapping("/atualizarForm/{id}")
    public String atualizarProdutoForm(@PathVariable(value = "id") Integer id, Model model) {
        ProdutoEntity produto = produtoService.getProdutoId(id);
        model.addAttribute("produto", produto);
        return "atualizarProduto";
    }

    @PostMapping("/atualizar")
    public String atualizarProduto(
            @Valid @ModelAttribute("produto") ProdutoEntity produto,
            BindingResult result) {

        if (result.hasErrors()) {
            return "atualizarProduto";
        }

        produtoService.atualizarProduto(produto.getId(), produto);
        return "redirect:/produto/listar";
    }
}
