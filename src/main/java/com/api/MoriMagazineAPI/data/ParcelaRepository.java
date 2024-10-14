package com.api.MoriMagazineAPI.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelaRepository extends JpaRepository<ParcelaEntity, Integer> { // Alterado para Integer

    List<ParcelaEntity> findByTransacaoId(Integer transacaoId); // Alterado para Integer

    @Query("SELECT p FROM ParcelaEntity p WHERE p.dataVencimento = :dataVencimento AND p.status = 'PENDENTE'")
    List<ParcelaEntity> findPendentesByDataVencimento(@Param("dataVencimento") LocalDate dataVencimento);

    @Query("SELECT p FROM ParcelaEntity p WHERE p.dataVencimento < :dataAtual AND p.status = 'PENDENTE'")
    List<ParcelaEntity> findAtrasadas(@Param("dataAtual") LocalDate dataAtual);

    @Query("SELECT p FROM ParcelaEntity p WHERE FUNCTION('MONTH', p.dataVencimento) = :mes AND FUNCTION('YEAR', p.dataVencimento) = :ano AND p.status = 'PENDENTE'")
    List<ParcelaEntity> findPendentesByMesEAno(@Param("mes") int mes, @Param("ano") int ano);

    @Query("SELECT p FROM ParcelaEntity p WHERE p.transacao.formaPagamento = 'Crediário' AND p.dataVencimento = :dataVencimento AND p.status = 'PENDENTE'")
    List<ParcelaEntity> findCrediarioPendentesByDataVencimento(@Param("dataVencimento") LocalDate dataVencimento);

    @Query("SELECT p FROM ParcelaEntity p WHERE p.transacao.formaPagamento = 'Cartão de Crédito' AND p.status = 'PENDENTE'")
    List<ParcelaEntity> findPendentesCartaoCredito();

    @Query("SELECT p FROM ParcelaEntity p WHERE p.dataVencimento < :dataAtual AND p.status = 'PENDENTE'")
    List<ParcelaEntity> findParcelasAtrasadas(@Param("dataAtual") LocalDate dataAtual);

    Optional<ParcelaEntity> findById(Integer id); // Alterado para Integer
}