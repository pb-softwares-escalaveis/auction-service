package org.infnet.auctionservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.domain.Bid;
import org.infnet.auctionservice.dto.BidRequest;
import org.infnet.auctionservice.events.bids.BidPlaced;
import org.infnet.auctionservice.events.lots.AuctionWinnerDefined;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.kafka.KafkaService;
import org.infnet.auctionservice.mocks.UserMock;
import org.infnet.auctionservice.mocks.UserServiceMock;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.repository.BidRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidService {
    private final UserServiceMock userServiceMock;
    private final KafkaService kafkaService;
    private final AuctionLotRepository lotRepository;
    private final BidRepository bidRepository;

    @Transactional
    public void placeBid(Long lotId, UUID bidderId, BidRequest request)  {

        // --- SIMULANDO REQ SINCRONA PARA USER-SERVICE
        UserMock bidder = userServiceMock.getUser(bidderId);

        if (bidder == null || !bidder.getAllowed()) {
            throw new UserNotAllowedException("Usuário não encontrado ou não autorizado.");
        }
        //---

        AuctionLot lot = lotRepository.findLockedById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        UserMock seller = userServiceMock.getUser(lot.getSellerId());

        lot.registerBid(request.bidAmount(), bidderId);

        Bid bid = new Bid(lot, bidderId, request.bidAmount());

        bidRepository.save(bid);
        lotRepository.save(lot);

        kafkaService.sendEvent(new BidPlaced(
                lot.getId(), seller.getId(), seller.getName(), seller.getEmail(),
                bidder.getId(), bidder.getName(), bidder.getEmail(),
                lot.getTitle(), lot.getMainImageUrl(), bid.getAmount(),
                Instant.now(), UUID.randomUUID()
        ));

        if (lot.getStatus().equals(AuctionStatus.SOLD)) {

            kafkaService.sendEvent(new AuctionWinnerDefined(
                    lot.getId(), seller.getId(), seller.getName(), seller.getEmail(),
                    bidder.getId(), bidder.getName(), bidder.getEmail(),
                    lot.getTitle(), lot.getMainImageUrl(), bid.getAmount(),
                    Instant.now(), UUID.randomUUID()
            ));
        }
    }
}