package org.infnet.auctionservice.repository;

import org.infnet.auctionservice.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid,Long> {
    @Modifying
    @Query("""
        UPDATE Bid b
        SET b.status = 'INVALID'
        WHERE b.bidderId = :bidderId
    """)
    void invalidateAllBidsFromUser(@Param("bidderId") UUID bidderId);

    @Query("""
        SELECT b FROM Bid b
        WHERE b.auctionLot.id = :lotId
        AND b.status = 'VALID'
        ORDER BY b.amount DESC
        LIMIT 1
    """)
    Optional<Bid> findHighestValidBidForLot(
            @Param("lotId") Long lotId
    );
}
