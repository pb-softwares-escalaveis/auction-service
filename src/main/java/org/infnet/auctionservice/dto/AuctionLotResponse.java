package org.infnet.auctionservice.dto;

import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.enums.CategoryEnum;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record AuctionLotResponse(
        Long id,
        UUID sellerId,
        String title,
        String description,
        BigDecimal initialBidPrice,
        BigDecimal currentBidPrice,
        BigDecimal buyNowPrice,
        CategoryEnum category,
        String mainImageUrl,
        AuctionStatus status,
        ZonedDateTime expirationDate) {
}
