package org.infnet.auctionservice.events.bids;

import org.infnet.auctionservice.events.AuctionEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BidPlaced(
        Long auctionId,
        UUID sellerId,
        String sellerName,
        String sellerEmail,
        UUID bidderId,
        String biddeName,
        String bidderEmail,
        String auctionTitle,
        String auctionThumb,
        BigDecimal amount,
        Instant ocurredAt,
        UUID correlationId

) implements AuctionEvent {
}
