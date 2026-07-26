package org.infnet.auctionservice.service.facade;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.infnet.auctionservice.dto.AuctionLotRequest;
import org.infnet.auctionservice.dto.AuctionLotResponse;
import org.infnet.auctionservice.dto.UserHeaderContext;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.service.AuctionLotService;
import org.infnet.auctionservice.storage.BucketStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuctionLotFacade {
    private final AuctionLotService auctionLotService;
    private final BucketStorageService bucketService;
    private final org.infnet.auctionservice.projection.UserProjectionRepository userProjectionRepository;

    public AuctionLotResponse createAuctionLot(UserHeaderContext user, AuctionLotRequest dto, MultipartFile image) throws Exception  {

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("A imagem do anúncio é obrigatória.");
        }

        boolean isAllowed = userProjectionRepository.findById(user.id())
                .map(projection -> "ACTIVE".equals(projection.getStatus()))
                .orElse(user.allowed());

        if (!isAllowed) {
            throw new UserNotAllowedException("Usuário não possui permissão para criar anúncios.");
        }

        validateImage(image);

        String imageBucketUrl = bucketService.uploadImage(image);

        return auctionLotService.registerAuctionLot(dto, imageBucketUrl, user);
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
