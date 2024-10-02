package com.api.MoriMagazineAPI.dto;

import com.api.MoriMagazineAPI.data.StatusParcela;
import com.api.MoriMagazineAPI.data.ParcelaEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ParcelaDetalhesDTO {

    private Long id; // Certifique-se de que este campo está presente
    private BigDecimal valorParcela;
    private LocalDate dataVencimento;
    private StatusParcela status;

    public ParcelaDetalhesDTO(ParcelaEntity parcela) {
        this.id = parcela.getId();
        this.valorParcela = parcela.getValorParcela();
        this.dataVencimento = parcela.getDataVencimento();
        this.status = parcela.getStatus();
    }
}
