package org.infnet.auctionservice.consumer;

import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.events.review.AuctionReviewApproved;
import org.infnet.auctionservice.events.review.AuctionReviewRejected;
import org.infnet.auctionservice.service.AuctionLotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewListener {
    private final AuctionLotService  lotService;

    @KafkaListener(topics = "${app.kafka-topics.review-approved}")
    public void consume(AuctionReviewApproved event){
        lotService.approveAuctionLot(event);
    }

    @KafkaListener(topics = "${app.kafka-topics.review-rejected}")
    public void consume(AuctionReviewRejected event){
        lotService.rejectAuctionLot(event);
    }
}
