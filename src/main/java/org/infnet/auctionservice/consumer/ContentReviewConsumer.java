package org.infnet.auctionservice.consumer;

import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.events.review.AuctionReviewApproved;
import org.infnet.auctionservice.events.review.AuctionReviewRejected;
import org.infnet.auctionservice.service.facade.AuctionLotFacade;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentReviewConsumer {
    private final AuctionLotFacade lotFacade;

    @KafkaListener(topics = "${app.kafka-topics.review-approved}")
    public void consume(AuctionReviewApproved event){
        lotFacade.processApprovedReview(event);
    }

    @KafkaListener(topics = "${app.kafka-topics.review-rejected}")
    public void consume(AuctionReviewRejected event){
        lotFacade.processRejectedReview(event);
    }
}
