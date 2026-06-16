package org.infnet.auctionservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.domain.Bid;
import org.infnet.auctionservice.dto.BidRequest;
import org.infnet.auctionservice.dto.UserHeaderContext;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.events.bid.BidPlaced;
import org.infnet.auctionservice.events.lot.AuctionEndedWithWinner;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.repository.BidRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {
    private final AuctionLotRepository lotRepository;
    private final BidRepository bidRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void placeBid(Long lotId, UserHeaderContext bidder, BidRequest request)  {
        AuctionLot lot = lotRepository.findLockedById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        if (!bidder.allowed()){
            throw new UserNotAllowedException("Usuário não autorizado.");
        }

        lot.registerBid(request.bidAmount(), bidder.id());

        Bid bid = new Bid(lot, bidder.id(), request.bidAmount());

        bidRepository.save(bid);
        lotRepository.save(lot);

        if (lot.getStatus() == AuctionStatus.SOLD){
            eventPublisher.publishEvent(new AuctionEndedWithWinner(
                    lot.getId(),
                    lot.getSellerId(),
                    lot.getHighestBidderId(),
                    lot.getTitle(),
                    lot.getMainImageUrl(),
                    lot.getCurrentBidPrice(),
                    Instant.now(),
                    UUID.randomUUID()
            ));
        } else {
            Instant now = Instant.now();

            eventPublisher.publishEvent(new BidPlaced(
                    lot.getId(),
                    lot.getSellerId(),
                    bidder.id(),
                    bidder.name(),
                    bidder.email(),
                    lot.getTitle(),
                    lot.getMainImageUrl(),
                    bid.getAmount(),
                    now,
                    now.toEpochMilli(),
                    UUID.randomUUID()
            ));
        }
    }
}