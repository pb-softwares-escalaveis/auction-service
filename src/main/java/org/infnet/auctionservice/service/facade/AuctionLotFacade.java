package org.infnet.auctionservice.service.facade;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.infnet.auctionservice.dto.AuctionLotRequest;
import org.infnet.auctionservice.dto.AuctionLotResponse;
import org.infnet.auctionservice.events.review.AuctionReviewApproved;
import org.infnet.auctionservice.events.review.AuctionReviewRejected;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.mocks.UserMock;
import org.infnet.auctionservice.mocks.UserServiceMock;
import org.infnet.auctionservice.service.AuctionLotService;
import org.infnet.auctionservice.storage.BucketStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionLotFacade {
    private final AuctionLotService auctionLotService;
    private final UserServiceMock userServiceMock;
    private final BucketStorageService bucketService;

    public AuctionLotResponse createAuctionLot(
            UUID userId,
            AuctionLotRequest dto,
            MultipartFile image) throws Exception  {

        // --- REQ SINCRONA USER-SERVICE
        UserMock user = getUser(userId);

        if (!user.getAllowed()) {
            throw new UserNotAllowedException("Usuário não autorizado.");
        }

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("A imagem do anúncio é obrigatória.");
        }

        validateImage(image);

        String imageBucketUrl = bucketService.uploadImage(image);

        return auctionLotService.registerAuctionLot(dto, imageBucketUrl, user);
    }

    public void deleteAuctionLot(UUID userId, Long lotId){
        UserMock user = getUser(userId);

        auctionLotService.removeAuctionLot(user, lotId);
    }

    public void processApprovedReview(AuctionReviewApproved event){
        UserMock user = getUser(event.sellerId());

        if (!user.getAllowed()) {
            throw new UserNotAllowedException("Usuário não autorizado.");
        }

        auctionLotService.approveAuctionLot(event, user);
    }

    public void processRejectedReview(AuctionReviewRejected event){
        UserMock user = getUser(event.sellerId());

        if (!user.getAllowed()) {
            throw new UserNotAllowedException("Usuário não autorizado.");
        }

        auctionLotService.rejectAuctionLot(event, user);
    }

    private UserMock getUser(UUID userId) {
        UserMock user = userServiceMock.getUser(userId);

        if (user == null) {
            throw new EntityNotFoundException("Usuário não encontrado com id: " + userId);
        }
        return user;
    }

    private void validateImage(MultipartFile image) throws IllegalArgumentException, IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("A imagem do anúncio é obrigatória.");
        }

        Tika tika = new Tika();

        if (!tika.detect(image.getInputStream()).startsWith("image/")) {
            throw new IOException("O arquivo enviado não é uma imagem válida.");
        }
    }
}
