package org.infnet.auctionservice.events.review;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionReviewApproved(
        Long auctionId,
        UUID sellerId,
        Instant occurredAt,
        UUID correlationId
) implements AuctionEvent {
}
