package com.api.MoriMagazineAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDTO {
    @NotNull
    private Integer produtoId; // Alterado para Integer

    @NotNull
    private int quantidade;
}
