package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.data.ProdutoRepository;
import com.api.MoriMagazineAPI.exeception.ResourceNotFoundException;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public ProdutoEntity criarProduto(ProdutoEntity produto) {
        produto.setId(null);
        produtoRepository.save(produto);
        return produto;
    }

    public ProdutoEntity atualizarProduto(Integer produtoId, ProdutoEntity produtoRequest) {
        ProdutoEntity produto = getProdutoId(produtoId);
        produto.setNomeProduto(produtoRequest.getNomeProduto());
        produto.setPreco(produtoRequest.getPreco());
        produto.setCompra(produtoRequest.getCompra());
        produtoRepository.save(produto);
        return produto;
    }

    public ProdutoEntity getProdutoId(Integer produtoId) {
        return produtoRepository.findById(produtoId).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado " + produtoId));
    }

    public List<ProdutoEntity> listarTodosProdutos() {
        return produtoRepository.findAll();
    }

    public void deletarProduto(Integer produtoId) {
        ProdutoEntity produto = getProdutoId(produtoId);
        produtoRepository.deleteById(produto.getId());
    }

    public List<ProdutoEntity> getProdutoPorNome(String nomeProduto) {
        return produtoRepository.findByNomeProdutoContaining(nomeProduto);
    }
public ProdutoEntity salvarProduto(ProdutoEntity produto) {
        return produtoRepository.save(produto);

}

}