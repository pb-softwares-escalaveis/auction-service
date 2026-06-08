package org.infnet.auctionservice.integrations;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.dto.UserStatusResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestClient restClient;

    public UserStatusResponse getUser(UUID userId) {
        UserStatusResponse user = restClient.get()
                .uri("/usuarios/{id}/status", userId)
                .retrieve()
                .body(UserStatusResponse.class);

        if (user == null) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }

        if (!user.allowed()) {
            throw new UserNotAllowedException("Usuário não autorizado.");
        }

        return user;
    }

}
