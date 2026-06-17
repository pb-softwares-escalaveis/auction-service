package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionBidReset(
        UUID correlationId,
        Long auctionId,
        UUID sellerId,
        String auctionTitle,
        String auctionThumb,
        Instant occurredAt
) implements AuctionEvent {
}
