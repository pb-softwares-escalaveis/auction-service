package org.infnet.auctionservice.events.user;

import java.time.Instant;
import java.util.UUID;

public record UserStatusChanged(
        UUID correlationId,
        UUID userId,
        Instant occurredAt
) {
}
