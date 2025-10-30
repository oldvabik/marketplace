package org.oldvabik.marketplace.dto;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class CardInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Long userId;
}
