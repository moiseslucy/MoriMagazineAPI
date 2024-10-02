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

    @Query("SELECT t FROM TransacaoEntity t WHERE FUNCTION('MONTH', t.dataTransacao) = :mes AND FUNCTION('YEAR', t.dataTransacao) = :ano")
    List<TransacaoEntity> findByDataTransacaoMonthAndYear(@Param("mes") int mes, @Param("ano") int ano);

    @Query("SELECT t FROM TransacaoEntity t JOIN t.produtosIds p WHERE p = :produtoId")
    List<TransacaoEntity> findByProdutosIdsContaining(@Param("produtoId") Long produtoId);

 @Query("SELECT i.produto FROM TransacaoEntity t JOIN t.itens i WHERE t.id = :transacaoId")
List<ProdutoEntity> findProdutosByTransacaoId(@Param("transacaoId") Long transacaoId);

}

