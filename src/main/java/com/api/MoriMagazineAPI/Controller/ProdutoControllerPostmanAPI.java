package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.service.ProdutoService;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produto/api")
public class ProdutoControllerPostmanAPI {

    @Autowired
    ProdutoService produtoService;

    @GetMapping("/listar")
    public ResponseEntity<List<ProdutoEntity>> getAllProdutos() {
        List<ProdutoEntity> produtos = produtoService.listarTodosProdutos();
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    @GetMapping("/pesquisar/{id}")
    public ResponseEntity<ProdutoEntity> getProdutoById(@PathVariable Integer id) {
        ProdutoEntity produto = produtoService.getProdutoId(id);
        return new ResponseEntity<>(produto, HttpStatus.OK);
    }

    @GetMapping("/pesquisar-nome/{nomeProduto}")
    public ResponseEntity<List<ProdutoEntity>> getPesquisarPorNomeProdutos(@PathVariable String nomeProduto) {
        List<ProdutoEntity> produtos = produtoService.getProdutoPorNome(nomeProduto);
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<ProdutoEntity> addProduto(@Valid @RequestBody ProdutoEntity produto) {
        var novoProduto = produtoService.criarProduto(produto);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }

    @PostMapping("/adicionar-lote")
    public ResponseEntity<List<ProdutoEntity>> addProdutos(@Valid @RequestBody List<ProdutoEntity> produtos) {
        List<ProdutoEntity> novosProdutos = produtoService.criarProdutos(produtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(novosProdutos);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ProdutoEntity> atualizarProduto(@PathVariable Integer id, @RequestBody ProdutoEntity produto) {
        var produtoAtualizado = produtoService.atualizarProduto(id, produto);
        return new ResponseEntity<>(produtoAtualizado, HttpStatus.OK);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity deletarProduto(@PathVariable Integer id) {
        produtoService.deletarProduto(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
