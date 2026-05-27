package org.infnet.auctionservice.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.domain.Bid;
import org.infnet.auctionservice.dto.BidRequest;
import org.infnet.auctionservice.exception.InvalidBidException;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.mocks.UserServiceMock;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.repository.BidRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BigDecimal MIN_BID_RATE = BigDecimal.valueOf(1.05);

    private final UserServiceMock userServiceMock;
    private final AuctionLotRepository lotRepository;
    private final BidRepository bidRepository;

    @Transactional
    public void placeBid(Long lotId, Long userId, BidRequest request) {
        if (!userServiceMock.isUserAllowed(userId)) {
            throw new UserNotAllowedException("Usuário não autorizado a realizar um lance");
        }

        AuctionLot lot = lotRepository.findLockedById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        if (lot.getSellerId().equals(userId)) {
            throw new UserNotAllowedException("O vendedor não pode dar lances em seu próprio anúncio");
        }

        if (lot.getExpirationDate().isBefore(java.time.ZonedDateTime.now())) {
            throw new InvalidBidException("O período de lances para este anúncio já expirou");
        }

        if (!lot.getStatus().equals(AuctionStatus.ACTIVE)) {
            throw new InvalidBidException("Anúncio não está aceitando lances");
        }

        BigDecimal minBidAmount = lot.getCurrentBidPrice().multiply(MIN_BID_RATE);

        if (request.bidAmount().compareTo(minBidAmount) < 0) {
            throw new InvalidBidException(String.format("O valor do lance mínimo atual é de R$ %.2f", minBidAmount));
        }

        Bid bid = new Bid();
        bid.setAuctionLot(lot);
        bid.setAmount(request.bidAmount());
        bid.setBidderId(userId);
        bid.setAmount(request.bidAmount());

        bidRepository.save(bid);
        lot.setCurrentBidPrice(request.bidAmount());

        if (lot.getBuyNowPrice() != null && request.bidAmount().compareTo(lot.getBuyNowPrice()) >= 0) {
            lot.setStatus(AuctionStatus.SOLD);
            lotRepository.save(lot);
        }


    }

}