package com.api.MoriMagazineAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDTO {
    @NotNull
    private Long produtoId;

    @NotNull
    private int quantidade;
}
