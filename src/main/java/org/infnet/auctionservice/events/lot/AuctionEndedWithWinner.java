package org.infnet.auctionservice.events.lot;

import org.infnet.auctionservice.events.AuctionEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionEndedWithWinner(
        Long auctionId,
        UUID sellerId,
        UUID highestBidderId,
        String auctionTitle,
        String auctionThumb,
        BigDecimal winnerBidValue,
        Instant occurredAt,
        UUID correlationId

) implements AuctionEvent {
}
