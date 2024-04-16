package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.data.ProdutoRepository;
import com.api.MoriMagazineAPI.exeception.ResourceNotFoundException;

import java.time.LocalDate;
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
public List<ProdutoEntity> criarProdutos(List<ProdutoEntity> produtos) {
    for (ProdutoEntity produto : produtos) {
        produto.setId(null); // Resetando o ID para garantir que um novo ID seja atribuído pelo banco de dados
    }
    List<ProdutoEntity> produtosSalvos = produtoRepository.saveAll(produtos);
    return produtosSalvos;
}
    
    
    public ProdutoEntity atualizarProduto(Integer produtoId, ProdutoEntity produtoRequest) {
        ProdutoEntity produto = getProdutoId(produtoId);
        if (produtoRequest.getNomeProduto() != null && !produtoRequest.getNomeProduto().isEmpty()) {
            produto.setNomeProduto(produtoRequest.getNomeProduto());
        }
        if (produtoRequest.getPreco() != null && !produtoRequest.getPreco().isEmpty()) {
            produto.setPreco(produtoRequest.getPreco());
        }
        if (produtoRequest.getDataCompra() != null) {
            produto.setDataCompra(produtoRequest.getDataCompra());
        }
        if (produtoRequest.getQuantidade() != null) {
            produto.setQuantidade(produtoRequest.getQuantidade());
        }
        produtoRepository.save(produto);
        return produto;
    }

    public ProdutoEntity getProdutoId(Integer produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado " + produtoId));
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

    public void atualizarNomeProduto(Integer id, String nomeProduto) {
        ProdutoEntity produto = getProdutoId(id);
        produto.setNomeProduto(nomeProduto);
        produtoRepository.save(produto);
    }

    public void atualizarPrecoProduto(Integer id, String preco) {
        String precoFormatado = preco.replace("R$", "").trim();
        ProdutoEntity produto = getProdutoId(id);
        produto.setPreco(precoFormatado);
        produtoRepository.save(produto);
    }

    public void atualizarDataCompraProduto(Integer id, LocalDate dataCompra) {
        ProdutoEntity produto = getProdutoId(id);
        produto.setDataCompra(dataCompra);
        produtoRepository.save(produto);
    }

    public void atualizarQuantidadeProduto(Integer id, Integer quantidade) {
        ProdutoEntity produto = getProdutoId(id);
        produto.setQuantidade(quantidade);
        produtoRepository.save(produto);
    }
}
