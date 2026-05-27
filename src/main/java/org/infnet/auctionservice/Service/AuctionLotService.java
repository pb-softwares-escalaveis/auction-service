package org.infnet.auctionservice.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.dto.AuctionLotRequest;
import org.infnet.auctionservice.dto.AuctionLotResponse;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.mocks.UserServiceMock;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.storage.BucketStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AuctionLotService {
    private final AuctionLotRepository lotRepository;
    private final UserServiceMock userServiceMock;
    private final BucketStorageService bucketService;

    public AuctionLotResponse getAuctionLot(Long lotId) {
        return toResponse(lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId)));
    }

    public Page<AuctionLotResponse> listAllActiveAuctionLots(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lotRepository.findByStatusEquals(AuctionStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public AuctionLotResponse createAuctionLot(Long userId, AuctionLotRequest dto, MultipartFile image) throws Exception {
        if (!userServiceMock.isUserAllowed(userId)){
            throw new UserNotAllowedException("Usuário não autorizado a criar um anúncio");
        }

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("A imagem do anúncio é obrigatória.");
        }

        String imageBucketUrl = bucketService.uploadImage(image);

        AuctionLot lot = new AuctionLot();
        lot.setSellerId(userId);
        lot.setName(dto.name());
        lot.setDescription(dto.description());
        lot.setInitialBidPrice(dto.initialBidPrice());
        lot.setBuyNowPrice(dto.buyNowPrice());
        lot.setCategory(dto.category());
        lot.setExpirationDate(ZonedDateTime.now().plusDays(dto.durationInDays()));
        lot.setMainImageUrl(imageBucketUrl);

        return  toResponse(lotRepository.save(lot));
    }

    @Transactional
    public void deleteAuctionLot(Long userId, Long lotId) {
        AuctionLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        if (!lot.getSellerId().equals(userId)) {
            throw new RuntimeException("Usuário não autorizado a deletar este anúncio");
        }

        lot.setStatus(AuctionStatus.REMOVED);
        lotRepository.save(lot);
    }

    private AuctionLotResponse toResponse(AuctionLot lot) {
        return new AuctionLotResponse(
                lot.getId(),
                lot.getSellerId(),
                lot.getName(),
                lot.getDescription(),
                lot.getInitialBidPrice(),
                lot.getCurrentBidPrice(),
                lot.getBuyNowPrice(),
                lot.getCategory(),
                lot.getMainImageUrl(),
                lot.getStatus(),
                lot.getExpirationDate());
    }
}

