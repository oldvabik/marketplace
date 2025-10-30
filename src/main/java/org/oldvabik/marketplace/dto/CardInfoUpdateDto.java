package org.oldvabik.marketplace.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CardInfoUpdateDto {
    @Size(min = 12, max = 32)
    private String number;

    @Future
    private LocalDate expirationDate;
}
