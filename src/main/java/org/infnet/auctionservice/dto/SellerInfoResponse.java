package org.infnet.auctionservice.dto;

import java.util.UUID;

public record SellerInfoResponse(
        UUID id,
        String name,
        String surname,
        String city,
        String country
) {
}
