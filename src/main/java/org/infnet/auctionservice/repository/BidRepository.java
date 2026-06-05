package org.infnet.auctionservice.repository;

import org.infnet.auctionservice.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid,Long> {
}
