package com.api.MoriMagazineAPI.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Long> {

    List<TransacaoEntity> findByClienteId(Long clienteId);
    List<TransacaoEntity> findByFormaPagamento(String formaPagamento);

    @Query("SELECT t FROM TransacaoEntity t WHERE FUNCTION('MONTH', t.dataTransacao) = :mes")
    List<TransacaoEntity> findByDataTransacaoMonth(@Param("mes") int mes);

    // Método para buscar transações pelo ID do produto (corrigido)
    @Query("SELECT t FROM TransacaoEntity t WHERE :produtoId MEMBER OF t.produtosIds") 
    List<TransacaoEntity> findByProdutosIdsContaining(@Param("produtoId") Long produtoId);
}

