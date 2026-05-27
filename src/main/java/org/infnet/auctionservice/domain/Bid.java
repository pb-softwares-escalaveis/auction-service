package org.infnet.auctionservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "bids")
@Getter@Setter

public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_lot_id", nullable = false)
    private AuctionLot auctionLot;
    @Column(name = "bidder_id", nullable = false)
    private Long bidderId;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }

}
