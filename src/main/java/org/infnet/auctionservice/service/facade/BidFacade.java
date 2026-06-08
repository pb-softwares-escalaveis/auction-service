package org.infnet.auctionservice.service.facade;

import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.dto.BidRequest;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.integrations.UserClient;
import org.infnet.auctionservice.dto.UserStatusResponse;
import org.infnet.auctionservice.service.BidService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidFacade {
    private final BidService bidService;
    private final UserClient userClient;

    public void placeBid(
            Long lotId,
            UUID bidderId,
            BidRequest request
    ){
        UserStatusResponse bidder = userClient.getUser(bidderId);

        if (!bidder.allowed()){
            throw new UserNotAllowedException("Usuário não autorizado.");
        }

        bidService.registerBid(lotId, bidder, request);
    }
}
