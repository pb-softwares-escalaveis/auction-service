package org.infnet.auctionservice.events.transaction;

import java.util.UUID;

public record TransactionClosed(
        Long transactionId,
        Long auctionId,
        UUID userId,
        UUID correlationId
) {
}
