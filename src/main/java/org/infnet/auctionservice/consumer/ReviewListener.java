package org.infnet.auctionservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.events.review.AuctionReviewApproved;
import org.infnet.auctionservice.events.review.AuctionReviewRejected;
import org.infnet.auctionservice.service.AuctionLotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewListener {
    private final AuctionLotService lotService;

    @KafkaListener(topics = "${app.kafka-topics.review-approved}")
    public void consume(AuctionReviewApproved event) {
        log.info("[REVIEW LISTENER] Evento consumido: revisão de anúncio aprovada. auctionId={}", event.auctionId());
        lotService.approveAuctionLot(event);
    }

    @KafkaListener(topics = "${app.kafka-topics.review-rejected}")
    public void consume(AuctionReviewRejected event) {
        log.info("[REVIEW LISTENER] Evento consumido: revisão de anúncio rejeitada. auctionId={}", event.auctionId());
        lotService.rejectAuctionLot(event);
    }

    @KafkaListener(topics = {"reviews.report.auction-approved"})
    public void consumeReport(AuctionReviewApproved event) {
        log.info("[REVIEW LISTENER] Evento consumido: reporte de anúncio aprovado. auctionId={}", event.auctionId());
        lotService.handleReportRemove(event);
    }
}
