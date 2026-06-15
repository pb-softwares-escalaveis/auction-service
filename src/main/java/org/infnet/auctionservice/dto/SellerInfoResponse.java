package org.infnet.auctionservice.dto;

import java.util.UUID;

public record SellerInfoResponse(
        UUID id,
        String fullName,
        String profilePic,
        String email,
        Float nota,
        String state,
        String city,
        String country
) {
}
