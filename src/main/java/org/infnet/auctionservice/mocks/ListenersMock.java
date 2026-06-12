package org.infnet.auctionservice.mocks;

import lombok.RequiredArgsConstructor;

import org.infnet.auctionservice.events.AuctionEvent;
import org.infnet.auctionservice.events.lot.AuctionCreatedPendingReview;
import org.infnet.auctionservice.events.review.AuctionReviewApproved;
import org.infnet.auctionservice.events.review.AuctionReviewRejected;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ListenersMock {
    private final KafkaTemplate<String, AuctionEvent> kafkaTemplate;

//    @KafkaListener(topics = {
    ////            "auctions.bid.placed",
    ////            "auctions.lot.approved",
    ////            "auction.lot.rejected",
    ////            "auctions.lot.ended-with-winner",
    ////            "auctions.lot.ended-without-winner",
    ////            "reviews.auction.approved",
    ////            "reviews.auction.rejected",
    ////            "auctions.lot.created-pending"
    ////    }, groupId = "qqcoisa")
    ////    public void consume(AuctionEvent event) {
    ////        System.out.printf("""
    ////                        [EVENT]
    ////                        - Event class %s
    ////                        - Auction ID: %s,
    ////                        - Occurred At: %s
    ////                        - Correlation ID: %s
    ////                        %n""",
    ////                event.getClass().getSimpleName(),
    ////                event.auctionId(),
    ////                event.ocurredAt(),
    ////                event.correlationId());
    ////    }

    @KafkaListener(topics = "auctions.lot.created-pending")
    public void consumeAuctionCreatedPendingReview(AuctionCreatedPendingReview event) throws InterruptedException {
        System.out.printf("""
                        [EVENT]
                        - Event class %s
                        - Auction ID: %s,
                        - Occurred At: %s
                        - Correlation ID: %s
                        %n""",
                event.getClass().getSimpleName(),
                event.auctionId(),
                event.ocurredAt(),
                event.correlationId());

        Thread.sleep(1000);
        double rng = Math.random();

        if (rng < 0.77) {
            kafkaTemplate.send("reviews.auction.approved", event.auctionId().toString(), new AuctionReviewApproved(
                    event.auctionId(),
                    event.sellerId(),
                    Instant.now(),
                    UUID.randomUUID()
            ));
        } else {
            kafkaTemplate.send("reviews.auction.rejected", event.auctionId().toString(),new AuctionReviewRejected(
                    event.auctionId(),
                    event.sellerId(),
                    Instant.now(),
                    "mock recusado",
                    UUID.randomUUID()
            ));
        }
    }
}

