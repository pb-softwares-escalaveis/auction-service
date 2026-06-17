package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.enums.AuctionLotCategory;
import org.infnet.auctionservice.events.AuctionEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionApproved(
        Long auctionId,
        UUID sellerId,
        String auctionTitle,
        String auctionThumb,
        Instant createdAt,
        AuctionLotCategory category,
        String description,
        Instant expirationDate,
        BigDecimal initialBidPrice,
        BigDecimal currentPrice,
        BigDecimal buyNowPrice,
        Instant occurredAt,
        UUID correlationId
) implements AuctionEvent {}
