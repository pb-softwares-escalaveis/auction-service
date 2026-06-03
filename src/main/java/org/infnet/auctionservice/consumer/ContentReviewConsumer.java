package org.infnet.auctionservice.consumer;

import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.events.review.AuctionReviewApproved;
import org.infnet.auctionservice.events.review.AuctionReviewRejected;
import org.infnet.auctionservice.service.AuctionLotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentReviewConsumer {
    private final AuctionLotService lotService;

    @KafkaListener(topics = "reviews.auction.approved")
    public void consume(AuctionReviewApproved event){
        lotService.processApprovedReview(event);
    }

    @KafkaListener(topics = "reviews.auction.rejected")
    public void consume(AuctionReviewRejected event){
        lotService.processRejectedReview(event);
    }
}
