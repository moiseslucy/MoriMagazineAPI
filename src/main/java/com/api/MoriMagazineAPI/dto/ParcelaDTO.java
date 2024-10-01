

package com.api.MoriMagazineAPI.dto;

import com.api.MoriMagazineAPI.data.StatusParcela;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ParcelaDTO {

    @NotNull
    private BigDecimal valorParcela;

    @NotNull
    private LocalDate dataVencimento;

    @NotNull
    private StatusParcela status;
}
