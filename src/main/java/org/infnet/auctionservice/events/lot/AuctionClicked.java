package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.enums.AuctionLotCategory;
import org.infnet.auctionservice.events.AuctionEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionClicked(
        Long auctionId,
        UUID userId,
        BigDecimal currentPrice,
        AuctionLotCategory category,
        Instant occurredAt,
        UUID correlationId
) implements AuctionEvent {
}
