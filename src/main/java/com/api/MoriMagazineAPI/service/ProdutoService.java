package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.data.ProdutoRepository;
import com.api.MoriMagazineAPI.exeception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public ProdutoEntity criarProduto(ProdutoEntity produto) {
        produto.setId(null);
        return produtoRepository.save(produto);
    }

    public ProdutoEntity atualizarProduto(Integer produtoId, ProdutoEntity produtoRequest) {
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

    public ProdutoEntity getProdutoId(Integer produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + produtoId));
    }

    public List<ProdutoEntity> listarTodosProdutos() {
        return produtoRepository.findAll();
    }

    public void deletarProduto(Integer produtoId) {
        ProdutoEntity produto = getProdutoId(produtoId);
        produtoRepository.delete(produto);
    }

    public List<ProdutoEntity> getProdutoPorNome(String nomeProduto) {
        return produtoRepository.findByNomeProdutoContainingIgnoreCase(nomeProduto);
    }

    public ProdutoEntity salvarProduto(ProdutoEntity produto) {
        return produtoRepository.save(produto);
    }

    public List<ProdutoEntity> listarProdutosPorIds(List<Integer> ids) {
        return produtoRepository.findAllById(ids);
    }

    public BigDecimal calcularValorTotal(List<Integer> produtosSelecionados) {
        return produtoRepository.findAllById(produtosSelecionados).stream()
                .map(ProdutoEntity::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getQuantidadeRestante(ProdutoEntity produto) {
        return produto.getQuantidade();
    }

    public List<ProdutoEntity> criarProdutos(List<ProdutoEntity> produtos) {
        produtos.forEach(produto -> produto.setId(null)); // Configura o ID como null para garantir criação de novos
        return produtoRepository.saveAll(produtos);
    }

    public void vender(ProdutoEntity produto, int quantidadeVendida) {
        if (produto.getQuantidade() >= quantidadeVendida) {
            produto.setQuantidade(produto.getQuantidade() - quantidadeVendida);
            produtoRepository.save(produto);
        } else {
            throw new IllegalArgumentException("Quantidade vendida maior que a disponível em estoque.");
        }
    }

    // Método para pesquisar produtos por termo (ID ou nome)
    public List<ProdutoEntity> pesquisarProdutos(String termo) {
        if (isNumeric(termo)) {
            Integer id = Integer.valueOf(termo);
            return produtoRepository.findById(id)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        } else {
            return produtoRepository.findByNomeProdutoContainingIgnoreCase(termo);
        }
    }

    // Método auxiliar para verificar se o termo é numérico
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}