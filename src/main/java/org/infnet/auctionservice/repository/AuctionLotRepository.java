package org.infnet.auctionservice.repository;

import jakarta.persistence.LockModeType;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionLotRepository extends JpaRepository<AuctionLot,Long> {
    List<AuctionLot> findByExpirationDateBefore(ZonedDateTime expirationDate);

    Page<AuctionLot> findByStatusEquals(AuctionStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AuctionLot> findLockedById(Long auctionLotId);
}
