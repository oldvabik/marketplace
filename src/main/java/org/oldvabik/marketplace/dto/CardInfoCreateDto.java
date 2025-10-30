package org.oldvabik.marketplace.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CardInfoCreateDto {
    @NotBlank
    @Size(min = 12, max = 32)
    private String number;

    @Future
    @NotNull
    private LocalDate expirationDate;

    @NotNull
    private Long userId;
}
