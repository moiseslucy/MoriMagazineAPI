package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.data.ProdutoRepository;
import com.api.MoriMagazineAPI.exeception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoEntity> getProdutosByIds(List<Long> produtoIds) {
        return produtoRepository.findAllById(produtoIds);
    }

    public ProdutoEntity criarProduto(ProdutoEntity produto) {
        produto.setId(null);
        return produtoRepository.save(produto);
    }

    public List<ProdutoEntity> criarProdutos(List<ProdutoEntity> produtos) {
        produtos.forEach(produto -> produto.setId(null));
        return produtoRepository.saveAll(produtos);
    }

    public ProdutoEntity atualizarProduto(Long produtoId, ProdutoEntity produtoRequest) {
        ProdutoEntity produto = getProdutoId(produtoId);
        if (produtoRequest.getNomeProduto() != null && !produtoRequest.getNomeProduto().isEmpty()) {
            produto.setNomeProduto(produtoRequest.getNomeProduto());
        }
        if (produtoRequest.getPreco() != null) {
            produto.setPreco(produtoRequest.getPreco());
        }
        if (produtoRequest.getDataCompra() != null) {
            produto.setDataCompra(produtoRequest.getDataCompra());
        }
        if (produtoRequest.getQuantidade() != null) {
            produto.setQuantidade(produtoRequest.getQuantidade());
        }
        return produtoRepository.save(produto);
    }

    public ProdutoEntity getProdutoId(Long produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado " + produtoId));
    }

    public List<ProdutoEntity> listarTodosProdutos() {
        return produtoRepository.findAll();
    }

    public void deletarProduto(Long produtoId) {
        ProdutoEntity produto = getProdutoId(produtoId);
        produtoRepository.delete(produto);
    }

    public List<ProdutoEntity> getProdutoPorNome(String nomeProduto) {
        return produtoRepository.findByNomeProdutoContaining(nomeProduto);
    }

    public ProdutoEntity salvarProduto(ProdutoEntity produto) {
        return produtoRepository.save(produto);
    }

    public void atualizarNomeProduto(Long id, String nomeProduto) {
        ProdutoEntity produto = getProdutoId(id);
        produto.setNomeProduto(nomeProduto);
        produtoRepository.save(produto);
    }

    public void atualizarPrecoProduto(Long id, BigDecimal preco) {
        ProdutoEntity produto = getProdutoId(id);
        produto.setPreco(preco);
        produtoRepository.save(produto);
    }

    public void atualizarDataCompraProduto(Long id, LocalDate dataCompra) {
        ProdutoEntity produto = getProdutoId(id);
        produto.setDataCompra(dataCompra);
        produtoRepository.save(produto);
    }

    public void atualizarQuantidadeProduto(Long id, Integer quantidade) {
        ProdutoEntity produto = getProdutoId(id);
        produto.setQuantidade(quantidade);
        produtoRepository.save(produto);
    }

    public List<ProdutoEntity> listarProdutos() {
        return produtoRepository.findAll();
    }
public List<ProdutoEntity> listarProdutosPorIds(List<Long> ids) {
    return produtoRepository.findAllById(ids);
}

}
