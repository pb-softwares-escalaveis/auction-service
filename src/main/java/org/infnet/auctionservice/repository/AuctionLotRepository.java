package org.infnet.auctionservice.repository;

import jakarta.persistence.LockModeType;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuctionLotRepository extends JpaRepository<AuctionLot,Long> {

    Page<AuctionLot> findAllByStatus(
            AuctionStatus status,
            Pageable pageable);

    @Query("""
    SELECT lot.id
    FROM AuctionLot lot
    WHERE lot.status = "ACTIVE"
    AND lot.expirationDate <= :now
    ORDER BY lot.expirationDate
    """)
    List<Long> findAllExpiredIds(
            @Param("now") Instant now,
            Pageable pageable
    );

    @Query(""" 
     SELECT l
     FROM AuctionLot l
     WHERE l.sellerId = :sellerId
     AND (:status IS NULL OR l.status = :status)
     """)
    Page<AuctionLot> findBySellerIdAndOptionalStatus(
            @Param("sellerId") UUID sellerId,
            @Param("status") AuctionStatus status,
            Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AuctionLot> findLockedById(Long auctionLotId);
}
