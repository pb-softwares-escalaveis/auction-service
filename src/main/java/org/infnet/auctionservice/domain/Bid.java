package org.infnet.auctionservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bids")
@Getter
@NoArgsConstructor
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_lot_id", nullable = false)
    private AuctionLot auctionLot;
    @Column(name = "bidder_id", nullable = false)
    private UUID bidderId;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Bid(AuctionLot auctionLot, UUID bidderId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do lance deve ser maior que zero.");
        }
        if (auctionLot == null || bidderId == null) {
            throw new IllegalArgumentException("Lance deve ter um leilão e um comprador associado.");
        }

        this.auctionLot = auctionLot;
        this.bidderId = bidderId;
        this.amount = amount;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

}
