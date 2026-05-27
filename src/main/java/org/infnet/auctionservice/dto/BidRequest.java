package org.infnet.auctionservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BidRequest(
        @NotNull(message = "Valor do lance é obrigatório")
        @Positive(message = "Valor do lance deve ser positivo")
        BigDecimal bidAmount
) {
}
