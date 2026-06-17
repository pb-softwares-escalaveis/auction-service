package org.infnet.auctionservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.enums.AuctionLotCategory;
import org.infnet.auctionservice.exception.InvalidBidException;
import org.infnet.auctionservice.exception.UserNotAllowedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auction_lots",
        check = {
            @CheckConstraint(name = "check_initial_bid_price_positive", constraint = "initial_bid_price > 0"),
            @CheckConstraint(name = "check_buy_now_price_positive", constraint = "buy_now_price > 0"),
            @CheckConstraint(name = "check_buy_now_price_higher_than_initial_bid", constraint = "buy_now_price > initial_bid_price")
},
        indexes = {
        @Index(
                name = "idx_auction_status_expiration",
                columnList = "status, expiration_date"
        )
})
@Getter
@Setter
@NoArgsConstructor
public class AuctionLot {
    private static final BigDecimal MIN_BID_RATE =
            BigDecimal.valueOf(1.05);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(nullable = false, length = 100)
    @Size(min = 5, max = 100)
    private String title;

    @Column(nullable = false, name = "description", length = 1200)
    @Size(min = 5, max = 1200)
    private String description;

    @Column(nullable = false, name = "initial_bid_price")
    private BigDecimal initialBidPrice;

    @Column(nullable = false, name = "current_bid_price")
    private BigDecimal currentBidPrice;

    @Column(nullable = true, name = "highest_bidder_id")
    private UUID highestBidderId;

    @Column(nullable = true, name = "buy_now_price")
    private BigDecimal buyNowPrice;

    @Column(name = "expiration_date")
    private Instant expirationDate;

    @Column(nullable = false, name = "main_image_url")
    private String mainImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionLotCategory category;

    @Column(name = "duration_in_days", nullable = false)
    private int durationInDays = 7;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public AuctionLot(UUID sellerId,
                      String title,
                      String description,
                      BigDecimal initialBidPrice,
                      BigDecimal buyNowPrice,
                      AuctionLotCategory category,
                      int durationInDays,
                      String mainImageUrl
    ) {
        if (initialBidPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Valor de inicial deve ser positivo."
            );
        }

        if (buyNowPrice != null &&
                buyNowPrice.compareTo(initialBidPrice) <= 0) {

            throw new IllegalArgumentException(
                    "Valor de arremate deve ser maior que o valor inicial."
            );
        }

        this.sellerId = sellerId;
        this.title = title;
        this.description = description;
        this.initialBidPrice = initialBidPrice;
        this.buyNowPrice = buyNowPrice;
        this.category = category;
        this.mainImageUrl = mainImageUrl;
        this.durationInDays = durationInDays;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.currentBidPrice = this.initialBidPrice;
        this.status = AuctionStatus.PENDING_REVIEW;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public void registerBid(BigDecimal bidAmount, UUID bidderId) {
        if (this.sellerId.equals(bidderId)) {
            throw new UserNotAllowedException("O vendedor não pode dar lances em seu próprio anúncio");
        }

        if (this.highestBidderId != null && this.highestBidderId.equals(bidderId)) {
            throw new InvalidBidException("Você já é o maior lance deste anúncio");
        }

        if (this.expirationDate.isBefore(Instant.now())) {
            throw new InvalidBidException("O período de lances para este anúncio já expirou");
        }

        if (!this.status.equals(AuctionStatus.ACTIVE)) {
            throw new InvalidBidException("Anúncio não está aceitando lances");
        }

        BigDecimal minBidAmount = this.currentBidPrice.multiply(MIN_BID_RATE);

        if (bidAmount.compareTo(minBidAmount) < 0) {
            throw new InvalidBidException(String.format("O valor do lance mínimo atual é de R$ %.2f", minBidAmount));
        }

        this.currentBidPrice = bidAmount;
        this.highestBidderId = bidderId;

        if (this.buyNowPrice != null && bidAmount.compareTo(this.buyNowPrice) >= 0) {
            this.status = AuctionStatus.SOLD;
        }
    }
}
