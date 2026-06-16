package org.infnet.auctionservice.kafka.listener;

import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.events.bid.BidPlaced;
import org.infnet.auctionservice.events.lot.*;
import org.infnet.auctionservice.kafka.service.KafkaSenderInterface;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class KafkaEventListener {
    private final KafkaSenderInterface sender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionApproved(AuctionApproved event) {
        sender.sendEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionCreatedPendingReview(AuctionCreatedPendingReview event) {
        sender.sendEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionRejected(AuctionRejected event) {
        sender.sendEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionEndedWithoutWinner(AuctionEndedWithoutWinner event) {
        sender.sendEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionEndedWithWinner(AuctionEndedWithWinner event) {
        sender.sendEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidPlaced(BidPlaced event) {
        sender.sendEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionRemoved(AuctionRemoved event) {
        sender.sendEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionRenewed(AuctionRenewed event){
        sender.sendEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionCanceled(AuctionCanceled event){
        sender.sendEvent(event);
    }

    @EventListener
    public void handleAuctionCanceled2(AuctionCanceled event){
        sender.sendEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNewHighestBidderAssigned(NewHighestBidderAssigned event){
        sender.sendEvent(event);
    }

    @Async
    @EventListener()
    public void handleAuctionClicked(AuctionClicked event){
        try {
            sender.sendEvent(event);
        } catch (Exception e) {
            System.out.println("DO SOMETHING WITH EXCEPTION (LOG)");
        }
    }
}
