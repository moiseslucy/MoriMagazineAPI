package com.api.MoriMagazineAPI.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Integer> {

    ProdutoEntity findByNomeProduto(String nomeProduto);

    List<ProdutoEntity> findByNomeProdutoContaining(String nomeProduto);

    // Outros métodos de consulta, se necessário
}
