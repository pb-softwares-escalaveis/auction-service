package org.infnet.auctionservice.repository;

import jakarta.persistence.LockModeType;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid,Long> {
    Optional<Bid> findTopByAuctionLotOrderByAmountDesc(AuctionLot auctionLot);
}
