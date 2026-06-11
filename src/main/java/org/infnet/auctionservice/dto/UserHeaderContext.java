package org.infnet.auctionservice.dto;

import java.util.UUID;

public record UserHeaderContext(UUID id, String name, String email, boolean allowed) {}
