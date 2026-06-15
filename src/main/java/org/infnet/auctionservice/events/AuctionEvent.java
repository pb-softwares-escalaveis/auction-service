package org.infnet.auctionservice.events;

import java.time.Instant;
import java.util.UUID;

public interface AuctionEvent {
    Long auctionId();
    Instant occurredAt();
    UUID correlationId();
}
