package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.events.AuctionEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record NewHighestBidderAssigned(
        UUID correlationId,
        Long auctionId,
        String auctionTitle,
        String auctionImage,
        UUID bidderId,
        String bidderName,
        String bidderEmail,
        Long bidId,
        BigDecimal amount,
        Instant ocurredAt

) implements AuctionEvent {
}
