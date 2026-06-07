package org.infnet.auctionservice.service.facade;

import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.dto.BidRequest;
import org.infnet.auctionservice.dto.BidResult;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.events.bids.BidPlaced;
import org.infnet.auctionservice.events.lots.AuctionEndedWithWinner;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.kafka.service.KafkaService;
import org.infnet.auctionservice.mocks.UserMock;
import org.infnet.auctionservice.mocks.UserServiceMock;
import org.infnet.auctionservice.service.BidService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidFacade {
    private final UserServiceMock userServiceMock;
    private final BidService bidService;
    private final KafkaService kafkaService;

    public void placeBid(
            Long lotId,
            UUID bidderId,
            BidRequest request
    ){

        // --- REQ SINCRONA USER-SERVICE
        UserMock bidder = userServiceMock.getUser(bidderId);
        if (bidder == null || !bidder.getAllowed()) {
            throw new UserNotAllowedException("Usuário não encontrado ou não autorizado.");
        }

        BidResult result = bidService.registerBid(lotId, bidder, request);

        kafkaService.sendEvent(new BidPlaced(
                result.lotId(),
                result.sellerId(),
                result.sellerName(),
                result.sellerEmail(),
                bidder.getId(),
                bidder.getName(),
                bidder.getEmail(),
                result.lotTitle(),
                result.lotImageUrl(),
                result.bidAmount(),
                Instant.now(),
                UUID.randomUUID()
        ));

        if (result.lotStatus().equals(AuctionStatus.SOLD)) {
            kafkaService.sendEvent(new AuctionEndedWithWinner(
                    result.lotId(),
                    result.sellerId(),
                    result.sellerName(),
                    result.sellerEmail(),
                    bidder.getId(),
                    result.secondBidderId(),
                    bidder.getName(),
                    bidder.getEmail(),
                    result.lotTitle(),
                    result.lotImageUrl(),
                    result.bidAmount(),
                    Instant.now(),
                    UUID.randomUUID()
            ));
        }
    }


}
