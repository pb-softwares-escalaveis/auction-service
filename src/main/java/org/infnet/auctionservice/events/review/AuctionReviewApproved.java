package org.infnet.auctionservice.events.review;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionReviewApproved(
        Long auctionId,
        Instant ocurredAt,
        UUID correlationId
) implements AuctionEvent {
}
