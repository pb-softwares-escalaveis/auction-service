package org.infnet.auctionservice.events.lots;

import org.infnet.auctionservice.events.AuctionEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionEndedWithWinner(
        Long auctionId,
        UUID sellerId,
        //String sellerName,
        //String sellerEmail,
        UUID highestBidderId,
        UUID secondHighestBidderId,
        //String bidderName,
        //String bidderEmail,
        String auctionTitle,
        String auctionThumb,
        BigDecimal winnerBidValue,
        Instant ocurredAt,
        UUID correlationId

) implements AuctionEvent {
}
