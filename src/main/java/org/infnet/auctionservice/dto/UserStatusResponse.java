package org.infnet.auctionservice.dto;

import java.util.UUID;

public record UserStatusResponse(
        UUID id,
        String name,
        String email,
        Boolean allowed
) {}
