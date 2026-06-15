package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionCanceled(
        UUID correlationId,
        Long auctionId,
        UUID highestBidderId,
        Instant occurredAt
) implements AuctionEvent {
}
