package org.infnet.auctionservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.enums.CategoryEnum;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "auction-lots", check = {
        @CheckConstraint(name = "check_initial_bid_price_positive", constraint = "initial_bid_price > 0"),
        @CheckConstraint(name = "check_buy_now_price_positive", constraint = "buy_now_price > 0"),
        @CheckConstraint(name = "expiration_date_in_future", constraint = "expiration_date > now()")
})
@Getter@Setter
public class AuctionLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "initial_bid_price")
    private BigDecimal initialBidPrice;

    @Column(nullable = false, name = "current_bid_price")
    private BigDecimal currentBidPrice;

    @Column(nullable = true, name = "buy_now_price")
    private BigDecimal buyNowPrice;

    @Column(nullable = false, name = "expiration_date")
    private ZonedDateTime expirationDate;

    @Column(nullable = false, name = "main_image_url")
    private String mainImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryEnum category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
        this.currentBidPrice = this.initialBidPrice;
        this.status = AuctionStatus.ACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

}
