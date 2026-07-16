package org.infnet.auctionservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.events.user.UserCreated;
import org.infnet.auctionservice.events.user.UserStatusChanged;
import org.infnet.auctionservice.projection.UserProjectionService;
import org.infnet.auctionservice.service.AuctionLotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserListener {
    private final AuctionLotService lotService;
    private final UserProjectionService projectionService;

    @KafkaListener(topics = {"${app.kafka-topics.user-suspended}"} )
    public void consumeSuspended(UserStatusChanged event){
        log.info("Recebido evento de SUSPENSÃO na conta do usuário: {}",event.userId());
        projectionService.updateStatus(event.userId(), "SUSPENDED");
        lotService.processUserPenalty(event);
    }

    @KafkaListener(topics = {"${app.kafka-topics.user-banned}"} )
    public void consumeBanned(UserStatusChanged event){
        log.info("Recebido evento de BANIMENTO na conta do usuário: {}",event.userId());
        projectionService.updateStatus(event.userId(), "BANNED");
        lotService.processUserPenalty(event);
    }

    @KafkaListener(topics = {"${app.kafka-topics.user-deleted}"} )
    public void consumeDeleted(UserStatusChanged event){
        log.info("Recebido evento de DELEÇÃO na conta do usuário: {}",event.userId());
        projectionService.updateStatus(event.userId(), "DELETED");
        lotService.processUserPenalty(event);
    }

    @KafkaListener(topics = {"${app.kafka-topics.user-created}"})
    public void consumeCreated(UserCreated event){
        log.info("Recebido evento de CRIAÇÃO do usuário: {}",event.userId());
        projectionService.saveProjection(event);
    }

}
