package org.infnet.auctionservice.dto;

import jakarta.validation.constraints.*;
import org.infnet.auctionservice.enums.CategoryEnum;

import java.math.BigDecimal;

public record AuctionLotRequest(
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 100, message = "O Título deve conter no máximo 100 caracteres")
        String title,
        @NotBlank(message = "Descrição é obrigatória")
        String description,
        @NotNull(message = "Valor do lance inicial é obrigátorio")
        @Positive(message = "Valor do lance inicial deve ser maior que zero")
        BigDecimal initialBidPrice,
        @Positive(message = "Valor de arremate deve ser maior que zero")
        BigDecimal buyNowPrice,
        @NotNull(message = "O prazo do anúncio é obrigatório.")
        @Positive(message = "O prazo de expiração deve ser de pelo menos 1 dia.")
        @Max(value = 7, message = "O prazo de expiração deve ser no máximo 7 dias.")
        Integer durationInDays,
        @NotNull(message = "Categoria é obrigatória")
        CategoryEnum category
) {
}
