package org.infnet.auctionservice.service.facade;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.infnet.auctionservice.dto.AuctionLotRequest;
import org.infnet.auctionservice.dto.AuctionLotResponse;
import org.infnet.auctionservice.events.review.AuctionReviewApproved;
import org.infnet.auctionservice.events.review.AuctionReviewRejected;
import org.infnet.auctionservice.integrations.UserClient;
import org.infnet.auctionservice.dto.UserStatusResponse;
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
    private final BucketStorageService bucketService;
    private final UserClient userClient;

    public AuctionLotResponse createAuctionLot(
            UUID userId,
            AuctionLotRequest dto,
            MultipartFile image)
            throws Exception  {

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("A imagem do anúncio é obrigatória.");
        }

        UserStatusResponse user = userClient.getUser(userId);

        validateImage(image);

        String imageBucketUrl = bucketService.uploadImage(image);

        return auctionLotService.registerAuctionLot(dto, imageBucketUrl, user);
    }

    public void deleteAuctionLot(UUID userId, Long lotId){
        UserStatusResponse user = userClient.getUser(userId);

        auctionLotService.removeAuctionLot(user, lotId);
    }

    public void processApprovedReview(AuctionReviewApproved event){
        UserStatusResponse user = userClient.getUser(event.sellerId());

        auctionLotService.approveAuctionLot(event, user);
    }

    public void processRejectedReview(AuctionReviewRejected event){
        UserStatusResponse user = userClient.getUser(event.sellerId());

        auctionLotService.rejectAuctionLot(event, user);
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
