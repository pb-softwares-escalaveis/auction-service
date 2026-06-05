package org.infnet.auctionservice.events.lots;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionRemoved(
        Long auctionId,
        UUID sellerId,
        String sellerName,
        String sellerEmail,
        String auctionTitle,
        String imageUrl,
        Instant ocurredAt,
        UUID correlationId
) implements AuctionEvent {
}
