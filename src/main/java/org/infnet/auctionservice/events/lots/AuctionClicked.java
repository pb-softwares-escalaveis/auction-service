package org.infnet.auctionservice.events.lots;

import org.infnet.auctionservice.enums.CategoryEnum;
import org.infnet.auctionservice.events.AuctionEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionClicked(
        Long auctionId,
        UUID userId,
        BigDecimal currentPrice,
        CategoryEnum category,
        Instant ocurredAt,
        UUID correlationId
) implements AuctionEvent {
}
