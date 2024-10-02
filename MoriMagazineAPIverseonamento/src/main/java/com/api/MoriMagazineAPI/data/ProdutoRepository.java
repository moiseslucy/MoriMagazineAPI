package com.api.MoriMagazineAPI.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {

    ProdutoEntity findByNomeProduto(String nomeProduto);

    List<ProdutoEntity> findByNomeProdutoContaining(String nomeProduto);

    List<ProdutoEntity> findAllByIdIn(List<Long> produtoIds);
}
