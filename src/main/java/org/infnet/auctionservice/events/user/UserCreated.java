package org.infnet.auctionservice.events.user;

import java.time.Instant;
import java.util.UUID;

public record UserCreated(
        UUID correlationId,
        UUID userId,
        String nome,
        String sobrenome,
        String fotoPerfil,
        String email,
        Float nota,
        String pais,
        String estado,
        String cidade,
        Instant occurredAt
) {
}
