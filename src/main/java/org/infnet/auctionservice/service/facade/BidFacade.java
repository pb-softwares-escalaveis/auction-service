package org.infnet.auctionservice.service.facade;

import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.dto.BidRequest;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.mocks.UserMock;
import org.infnet.auctionservice.mocks.UserServiceMock;
import org.infnet.auctionservice.service.BidService;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidFacade {
    private final UserServiceMock userServiceMock;
    private final BidService bidService;

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

        bidService.registerBid(lotId, bidder, request);
    }
}
