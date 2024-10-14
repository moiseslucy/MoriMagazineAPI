package com.api.MoriMagazineAPI.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Integer> { // Alterado para Integer

    List<TransacaoEntity> findByCliente_Id(Integer clienteId); // Alterado para Integer

    List<TransacaoEntity> findByFormaPagamento(String formaPagamento);

    @Query("SELECT t FROM TransacaoEntity t WHERE FUNCTION('MONTH', t.dataTransacao) = :mes")
    List<TransacaoEntity> findByDataTransacaoMonth(@Param("mes") int mes);

    @Query("SELECT t FROM TransacaoEntity t WHERE FUNCTION('MONTH', t.dataTransacao) = :mes AND FUNCTION('YEAR', t.dataTransacao) = :ano")
    List<TransacaoEntity> findByDataTransacaoMonthAndYear(@Param("mes") int mes, @Param("ano") int ano);

    @Query("SELECT t FROM TransacaoEntity t WHERE FUNCTION('MONTH', t.dataTransacao) = :mes AND FUNCTION('YEAR', t.dataTransacao) = :ano AND t.formaPagamento = :formaPagamento")
    List<TransacaoEntity> findByDataTransacaoMonthAndYearAndFormaPagamento(@Param("mes") int mes, @Param("ano") int ano, @Param("formaPagamento") String formaPagamento);

    @Query("SELECT t FROM TransacaoEntity t JOIN t.itens i WHERE i.produto = :produto")
    List<TransacaoEntity> findByProduto(@Param("produto") ProdutoEntity produto);

    @Query("SELECT t FROM TransacaoEntity t WHERE t.cliente.nome LIKE %:nomeCliente%")
    List<TransacaoEntity> findByClienteNome(@Param("nomeCliente") String nomeCliente);
}