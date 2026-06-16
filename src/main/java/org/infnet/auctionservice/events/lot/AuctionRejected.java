package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionRejected(
        Long auctionId,
        UUID sellerId,
        String reason,
        String auctionTitle,
        String auctionThumb,
        Instant occurredAt,
        Instant createdAt,
        UUID correlationId
) implements AuctionEvent {
}
