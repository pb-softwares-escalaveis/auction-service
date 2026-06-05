package org.infnet.auctionservice.dto;

import org.infnet.auctionservice.enums.AuctionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record BidResult(
        Long lotId,
        UUID sellerId,
        String lotTitle,
        AuctionStatus lotStatus,
        String lotImageUrl,
        BigDecimal bidAmount,
        UUID secondBidderId
) {
}
