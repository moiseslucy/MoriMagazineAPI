package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.service.ProdutoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    ProdutoService produtoService;

    @GetMapping("/listar")
    public String viewProdutosPage(Model model) {
        model.addAttribute("listarProdutos", produtoService.listarTodosProdutos());
        return "produtoIndex";
    }

    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable(value = "id") Integer id) {
        produtoService.deletarProduto(id);
        return "redirect:/produto/listar";
    }

    @GetMapping("/criarForm")
    public String criarProdutoForm(Model model) {
        ProdutoEntity produto = new ProdutoEntity();
        model.addAttribute("produto", produto);
        return "inserirProduto";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoEntity produto, BindingResult result) {
        if (result.hasErrors()) {
            return "inserirProduto";
        }
        if (produto.getId() == null) {
            produtoService.criarProduto(produto);
        } else {
            produtoService.atualizarProduto(produto.getId(), produto);
        }
        return "redirect:/produto/listar";
    }

    @GetMapping("/atualizarForm/{id}")
    public String atualizarProdutoForm(@PathVariable(value = "id") Integer id, Model model) {
        ProdutoEntity produto = produtoService.getProdutoId(id);
        model.addAttribute("produto", produto);
        return "atualizarProduto";
    }
}