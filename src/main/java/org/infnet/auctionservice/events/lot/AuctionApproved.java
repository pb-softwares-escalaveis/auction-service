package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionApproved(
        Long auctionId,
        UUID sellerId,
        String auctionTitle,
        String auctionThumb,
        Instant createdAt,
        Instant occurredAt,
        UUID correlationId
) implements AuctionEvent {}
