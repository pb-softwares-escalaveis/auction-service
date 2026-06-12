package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.events.AuctionEvent;

import java.time.Instant;
import java.util.UUID;

public record AuctionEndedWithoutWinner(
        Long auctionId,
        UUID sellerId,
        //String sellerName,
        //String sellerEmail,
        String auctionTitle,
        String auctionThumb,
        Instant ocurredAt,
        UUID correlationId
) implements AuctionEvent {
}
