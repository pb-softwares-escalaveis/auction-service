package org.infnet.auctionservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.domain.Bid;
import org.infnet.auctionservice.dto.BidRequest;
import org.infnet.auctionservice.dto.BidResult;
import org.infnet.auctionservice.mocks.UserMock;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.repository.BidRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {
    private final AuctionLotRepository lotRepository;
    private final BidRepository bidRepository;

    @Transactional
    public BidResult registerBid(Long lotId, UserMock bidder, BidRequest request)  {
        AuctionLot lot = lotRepository.findLockedById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        lot.registerBid(request.bidAmount(), bidder.getId());

        Bid bid = new Bid(
                lot,
                bidder.getId(),
                request.bidAmount());

        bidRepository.save(bid);
        lotRepository.save(lot);

        return new BidResult(
                lot.getId(),
                lot.getSellerId(),
                lot.getTitle(),
                lot.getStatus(),
                lot.getMainImageUrl(),
                bid.getAmount(),
                lot.getSecondHighetBidderId()
        );
    }
}