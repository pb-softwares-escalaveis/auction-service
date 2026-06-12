package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.events.AuctionEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record NewHighestBidderAssigned(
        UUID correlationId,
        Long auctionId,
        UUID bidderId,
        Long bidId,
        BigDecimal amount,
        Instant ocurredAt

) implements AuctionEvent {
}
