package org.infnet.auctionservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.enums.CategoryEnum;
import org.infnet.auctionservice.exception.InvalidBidException;
import org.infnet.auctionservice.exception.UserNotAllowedException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "auction-lots", check = {
        @CheckConstraint(name = "check_initial_bid_price_positive", constraint = "initial_bid_price > 0"),
        @CheckConstraint(name = "check_buy_now_price_positive", constraint = "buy_now_price > 0"),
        @CheckConstraint(name = "expiration_date_in_future", constraint = "expiration_date > now()")
})
@Getter@Setter
@NoArgsConstructor
public class AuctionLot {
    private final BigDecimal MIN_BID_RATE = BigDecimal.valueOf(1.05);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "initial_bid_price")
    private BigDecimal initialBidPrice;

    @Column(nullable = false, name = "current_bid_price")
    private BigDecimal currentBidPrice;

    @Column(nullable = true, name = "buy_now_price")
    private BigDecimal buyNowPrice;

    @Column(name = "expiration_date")
    private ZonedDateTime expirationDate;

    @Column(nullable = false, name = "main_image_url")
    private String mainImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryEnum category;

    @Column(name = "duration_in_days", nullable = false)
    private int durationInDays = 7;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public AuctionLot(UUID sellerId, String title, String description,
                      BigDecimal initialBidPrice, BigDecimal buyNowPrice,
                      CategoryEnum category, int durationInDays, String mainImageUrl) {

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
    public void onCreate(){
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
        this.currentBidPrice = this.initialBidPrice;
        this.status = AuctionStatus.PENDING_REVIEW;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

    public void registerBid(BigDecimal bidAmount, UUID bidderId){
        if (this.sellerId.equals(bidderId)) {
            throw new UserNotAllowedException("O vendedor não pode dar lances em seu próprio anúncio");
        }

        if (this.expirationDate.isBefore(ZonedDateTime.now())) {
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

        if (this.buyNowPrice != null && bidAmount.compareTo(this.buyNowPrice) >= 0) {
            this.status = AuctionStatus.SOLD;
        }
    }

    public void removeLot(UUID userId) {
        if (!this.sellerId.equals(userId)) {
            throw new UserNotAllowedException("Usuário não autorizado a deletar este anúncio");
        }
        this.status = AuctionStatus.REMOVED;
    }
}
