package org.infnet.auctionservice.projection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.events.user.UserCreated;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProjectionService {
    private final UserProjectionRepository projectionRepository;

    public void saveProjection(UserCreated event){
        UserProjection projection = new UserProjection();
        projection.setId(event.userId());
        projection.setFullName(event.nome() + " " + event.sobrenome());
        projection.setProfilePic(event.fotoPerfil());
        projection.setEmail(event.email());
        projection.setScore(event.nota());
        projection.setCountry(event.pais());
        projection.setState(event.estado());
        projection.setCity(event.cidade());
        projection.setCreatedAt(event.occurredAt());

        try {
            projectionRepository.save(projection);
        } catch (DataIntegrityViolationException e) {
            log.info("Evento UserCreated duplicado ignorado com userId: {} e correlationId: {}", event.userId(), event.correlationId());
        }
    }

}
