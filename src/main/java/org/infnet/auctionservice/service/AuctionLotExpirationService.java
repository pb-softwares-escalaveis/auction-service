package org.infnet.auctionservice.service;

import org.infnet.auctionservice.utils.CorrelationIdUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.events.lot.AuctionEndedWithWinner;
import org.infnet.auctionservice.events.lot.AuctionEndedWithoutWinner;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionLotExpirationService {
    private final AuctionLotRepository lotRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void processEnd(Long lotId) {
        AuctionLot lot = lotRepository.findLockedById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        if (!lot.getExpirationDate().isBefore(Instant.now())) return;

        if (!lot.getStatus().equals(AuctionStatus.ACTIVE)) return;

        if (lot.getHighestBidderId() == null){
            lot.setStatus(AuctionStatus.EXPIRED);
            lotRepository.save(lot);

            log.info("[AUCTION LOT EXPIRATION SERVICE] Processamento de expiração concluído. auctionId={} resultado=SEM_VENCEDOR", lotId);

            eventPublisher.publishEvent(
                    new AuctionEndedWithoutWinner(
                            lot.getId(),
                            lot.getSellerId(),
                            lot.getTitle(),
                            lot.getMainImageUrl(),
                            Instant.now(),
                            CorrelationIdUtil.getCorrelationIdAsUUID()
                    )
            );
        } else {
            lot.setStatus(AuctionStatus.SOLD);
            lotRepository.save(lot);

            log.info("[AUCTION LOT EXPIRATION SERVICE] Processamento de expiração concluído. auctionId={} resultado=VENDIDO highestBidderId={}", lotId, lot.getHighestBidderId());

            eventPublisher.publishEvent(new AuctionEndedWithWinner(
                    lot.getId(),
                    lot.getSellerId(),
                    lot.getHighestBidderId(),
                    lot.getTitle(),
                    lot.getMainImageUrl(),
                    lot.getCurrentBidPrice(),
                    Instant.now(),
                    CorrelationIdUtil.getCorrelationIdAsUUID()
            ));
        }
    }
}
