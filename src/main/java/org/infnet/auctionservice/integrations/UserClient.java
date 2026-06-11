package org.infnet.auctionservice.integrations;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.dto.SellerInfoResponse;
import org.infnet.auctionservice.dto.UserStatusResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestClient restClient;

    public UserStatusResponse getUser(UUID userId) {
        return restClient.get()
                .uri("/usuarios/{id}/status", userId)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new EntityNotFoundException("Usuário não encontrado com id: " + userId);
                })
                .body(UserStatusResponse.class);
    }

    public SellerInfoResponse getSellerInfo(UUID sellerId) {
        return restClient.get()
                .uri("/usuarios/id/seller-info")
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new EntityNotFoundException("Usuário não encontrado com id: " + sellerId);
                })
                .body(SellerInfoResponse.class);
    }

}
