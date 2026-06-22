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
        log.info("[USER LISTENER] Evento consumido: suspensão de usuário. userId={}",event.userId());
        lotService.processUserPenalty(event);
    }

    @KafkaListener(topics = {"${app.kafka-topics.user-banned}"} )
    public void consumeBanned(UserStatusChanged event){
        log.info("[USER LISTENER] Evento consumido: banimento de usuário. userId={}",event.userId());
        lotService.processUserPenalty(event);
    }

    @KafkaListener(topics = {"${app.kafka-topics.user-deleted}"} )
    public void consumeDeleted(UserStatusChanged event){
        log.info("[USER LISTENER] Evento consumido: deleção de usuário. userId={}",event.userId());
        lotService.processUserPenalty(event);
    }

    @KafkaListener(topics = {"${app.kafka-topics.user-created}"})
    public void consumeCreated(UserCreated event){
        log.info("[USER LISTENER] Evento consumido: criação de usuário. userId={}",event.userId());
        projectionService.saveProjection(event);
    }

}
