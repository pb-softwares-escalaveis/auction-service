package org.infnet.auctionservice.dto;

import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.enums.AuctionLotCategory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionLotResponse(
        Long id,
        UUID sellerId,
        String title,
        String description,
        BigDecimal initialBidPrice,
        BigDecimal currentBidPrice,
        BigDecimal buyNowPrice,
        AuctionLotCategory category,
        String mainImageUrl,
        AuctionStatus status,
        Instant expirationDate
) {
}
