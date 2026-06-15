package org.infnet.auctionservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.events.transaction.TransactionClosed;
import org.infnet.auctionservice.service.AuctionLotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionListener {
    private final AuctionLotService lotService;

    @KafkaListener(topics = "${app.kafka-topics.transaction-closed}")
    public void consumePaymentFail(TransactionClosed event){
        log.info("Recebido evento de falha na transação {}, relacionada ao anúncio: {}",event.transactionId(), event.auctionId());
        lotService.handleTransactionFailure(event);
    }
}