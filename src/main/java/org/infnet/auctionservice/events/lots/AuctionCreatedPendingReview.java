package org.infnet.auctionservice.events.lots;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionCreatedPendingReview(
        Long auctionId,
        UUID sellerId,
        String sellerName,
        String sellerEmail,
        String auctionTitle,
        Instant ocurredAt,
        String auctionThumb,
        UUID correlationId
) implements AuctionEvent {
}
