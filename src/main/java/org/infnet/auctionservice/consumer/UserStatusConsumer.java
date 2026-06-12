package org.infnet.auctionservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.events.user.UserStatusChanged;
import org.infnet.auctionservice.service.AuctionLotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserStatusConsumer {
    private final AuctionLotService lotService;

    @KafkaListener(topics = {"${app.kafka-topics.user-suspended}"} )
    public void consumeSuspended(UserStatusChanged event){
        log.info("Recebido evento de SUSPENSÃO na conta do usuário: {}",event.userId());
        lotService.processUserPenalty(event);
    }

    @KafkaListener(topics = {"${app.kafka-topics.user-banned}"} )
    public void consumeBanned(UserStatusChanged event){
        log.info("Recebido evento de BANIMENTO na conta do usuário: {}",event.userId());
        lotService.processUserPenalty(event);
    }

    @KafkaListener(topics = {"${app.kafka-topics.user-deleted}"} )
    public void consumeDeleted(UserStatusChanged event){
        log.info("Recebido evento de DELEÇÃO na conta do usuário: {}",event.userId());
        lotService.processUserPenalty(event);
    }

}
