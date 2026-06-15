package org.infnet.auctionservice.events.review;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionReviewRejected(
        Long auctionId,
        UUID sellerId,
        Instant occurredAt,
        String reason,
        UUID correlationId
) implements AuctionEvent {
}