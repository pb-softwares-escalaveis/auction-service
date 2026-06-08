package org.infnet.auctionservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.events.lots.AuctionEndedWithWinner;
import org.infnet.auctionservice.events.lots.AuctionEndedWithoutWinner;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

            eventPublisher.publishEvent(
                    new AuctionEndedWithoutWinner(
                            lot.getId(),
                            lot.getSellerId(),
                            lot.getTitle(),
                            lot.getMainImageUrl(),
                            Instant.now(),
                            UUID.randomUUID()
                    )
            );
        } else {
            lot.setStatus(AuctionStatus.SOLD);
            lotRepository.save(lot);

            eventPublisher.publishEvent(new AuctionEndedWithWinner(
                    lot.getId(),
                    lot.getSellerId(),
                    lot.getHighestBidderId(),
                    lot.getSecondHighetBidderId(),
                    lot.getTitle(),
                    lot.getMainImageUrl(),
                    lot.getCurrentBidPrice(),
                    Instant.now(),
                    UUID.randomUUID()
            ));
        }
    }
}
