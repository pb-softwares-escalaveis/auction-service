package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionCreatedPendingReview(
        Long auctionId,
        UUID sellerId,
        String sellerName,
        String sellerEmail,
        String auctionTitle,
        String auctionDescription,
        Instant occurredAt,
        String auctionThumb,
        UUID correlationId
) implements AuctionEvent {
}
