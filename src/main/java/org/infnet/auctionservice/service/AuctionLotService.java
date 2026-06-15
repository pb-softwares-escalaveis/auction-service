package org.infnet.auctionservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.dto.*;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.events.lot.*;
import org.infnet.auctionservice.events.review.AuctionReviewApproved;
import org.infnet.auctionservice.events.review.AuctionReviewRejected;
import org.infnet.auctionservice.events.transaction.TransactionClosed;
import org.infnet.auctionservice.events.user.UserStatusChanged;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.projection.UserProjection;
import org.infnet.auctionservice.projection.UserProjectionRepository;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.repository.BidRepository;
import org.infnet.auctionservice.storage.BucketStorageService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionLotService {
    private final AuctionLotRepository lotRepository;
    private final BucketStorageService bucketService;
    private final ApplicationEventPublisher eventPublisher;
    private final BidRepository bidRepository;
    private final UserProjectionRepository projectionRepository;

    public AuctionLotWithSellerInfo getFullAuctionLot(Long lotId, UUID userId) {
        AuctionLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        UserProjection sellerInfo = projectionRepository.findById(lot.getSellerId()).orElse(null);

        AuctionLotWithSellerInfo completeLot = new AuctionLotWithSellerInfo(
                lot.getId(),
                lot.getSellerId(),
                sellerInfo != null ? sellerInfo.getFullName() : "Nome Completo",
                sellerInfo != null ? sellerInfo.getCity() : "Cidade",
                sellerInfo != null ? sellerInfo.getCountry() : "País",
                sellerInfo != null ? sellerInfo.getState() : "Estado",
                sellerInfo != null ? sellerInfo.getProfilePic() : "https://bucket.oleiloeiroonline.top/profile-images/default-pfp.jpg",
                sellerInfo != null ? sellerInfo.getScore() : 3F,
                lot.getTitle(),
                lot.getDescription(),
                lot.getInitialBidPrice(),
                lot.getCurrentBidPrice(),
                lot.getBuyNowPrice(),
                lot.getCategory(),
                lot.getMainImageUrl(),
                lot.getStatus(),
                lot.getExpirationDate());

        if (userId == null) {
            return completeLot;
        }

        eventPublisher.publishEvent(new AuctionClicked(
                lot.getId(),
                userId,
                lot.getCurrentBidPrice(),
                lot.getCategory(),
                ZonedDateTime.now().toInstant(),
                UUID.randomUUID()));

        return completeLot;
    }

    //responsabilidade do listing-service - REMOVER DEPOIS
    public Page<AuctionLotResponse> listAllActiveAuctionLots(int page, int size) {
        return lotRepository.findAllByStatus(AuctionStatus.ACTIVE, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Transactional
    public Page<AuctionLotResponse> listAllUserLotsByStatus(UUID userId, AuctionStatus status, int page, int size) {
        return  lotRepository.findBySellerIdAndOptionalStatus(userId, status, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Transactional
    public AuctionLotResponse registerAuctionLot(AuctionLotRequest dto, String imageUrl, UserHeaderContext user) {
        AuctionLot lot = new AuctionLot(
                user.id(),
                dto.title(),
                dto.description(),
                dto.initialBidPrice(),
                dto.buyNowPrice(),
                dto.category(),
                dto.durationInDays(),
                imageUrl
        );

        AuctionLotResponse response;
        try {
            response = toResponse(lotRepository.save(lot));
        } catch (Exception e) {
            bucketService.deleteImage(imageUrl);
            throw new RuntimeException("Erro ao salvar o anúncio: " + e.getMessage());
        }

        eventPublisher.publishEvent(new AuctionCreatedPendingReview(
                lot.getId(),
                lot.getSellerId(),
                user.name(),
                user.email(),
                lot.getTitle(),
                lot.getDescription(),
                Instant.now(),
                lot.getMainImageUrl(),
                UUID.randomUUID()
        ));

        return response;
    }

    @Transactional
    public void removeAuctionLot(UserHeaderContext ctx, Long lotId) {

        AuctionLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        if (!lot.getSellerId().equals(ctx.id())) {
            throw new UserNotAllowedException("Usuário não pode deletar um anúncio que não é seu.");
        }

        lot.setStatus(AuctionStatus.REMOVED);
        lotRepository.save(lot);

        eventPublisher.publishEvent(new AuctionRemoved(
                lot.getId(),
                lot.getSellerId(),
                ctx.email(),
                ctx.name(),
                lot.getTitle(),
                lot.getMainImageUrl(),
                Instant.now(),
                UUID.randomUUID()
        ));
    }

    @Transactional
    public void approveAuctionLot(AuctionReviewApproved event) {
        AuctionLot lot = lotRepository.findById(event.auctionId())
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + event.auctionId()));

        if (lot.getStatus() != AuctionStatus.PENDING_REVIEW) {
            log.warn("Ignorando aprovação. O anúncio {} não está pendente. Status atual: {}", lot.getId(), lot.getStatus());
            return;
        }

        lot.setStatus(AuctionStatus.ACTIVE);
        lot.setExpirationDate(Instant.now().plus(lot.getDurationInDays(), ChronoUnit.DAYS));
        lotRepository.save(lot);

        eventPublisher.publishEvent(new AuctionApproved(
                lot.getId(),
                lot.getSellerId(),
                lot.getTitle(),
                lot.getMainImageUrl(),
                lot.getCreatedAt(),
                Instant.now(),
                UUID.randomUUID()
        ));
    }

    @Transactional
    public void rejectAuctionLot(AuctionReviewRejected event) {
        AuctionLot lot = lotRepository.findById(event.auctionId())
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + event.auctionId()));

        if (lot.getStatus() != AuctionStatus.PENDING_REVIEW) {
            log.warn("Ignorando aprovação. O anúncio {} não está pendente. Status atual: {}", lot.getId(), lot.getStatus());
            return;
        }

        lot.setStatus(AuctionStatus.REJECTED);
        lot.setExpirationDate(Instant.now().minus(lot.getDurationInDays(), ChronoUnit.DAYS));
        lotRepository.save(lot);

        eventPublisher.publishEvent(new AuctionRejected(
                lot.getId(),
                lot.getSellerId(),
                event.reason(),
                lot.getTitle(),
                lot.getMainImageUrl(),
                Instant.now(),
                UUID.randomUUID()
        ));
    }

    @Transactional
    public AuctionLotResponse renewAuctionLot(Long lotId, UserHeaderContext ctx) {
        if (!ctx.allowed()){
            throw new UserNotAllowedException("Usuário não autorizado.");
        }

        AuctionLot oldLot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        if (oldLot.getStatus() != AuctionStatus.EXPIRED) {
            throw new IllegalStateException("Apenas anúncios expirados podem ser renovados.");
        }

        if (!oldLot.getSellerId().equals(ctx.id())) {
            throw new UserNotAllowedException("Apenas o criador pode renovar este anúncio.");
        }

        AuctionLot newLot = new AuctionLot(
                oldLot.getSellerId(),
                oldLot.getTitle(),
                oldLot.getDescription(),
                oldLot.getInitialBidPrice(),
                oldLot.getBuyNowPrice(),
                oldLot.getCategory(),
                oldLot.getDurationInDays(),
                oldLot.getMainImageUrl()
        );

        newLot.setStatus(AuctionStatus.ACTIVE);
        newLot.setExpirationDate(Instant.now().plus(newLot.getDurationInDays(), ChronoUnit.DAYS));

        eventPublisher.publishEvent(new AuctionRenewed(
                UUID.randomUUID(),
                newLot.getId(),
                newLot.getSellerId(),
                newLot.getTitle(),
                newLot.getMainImageUrl(),
                Instant.now()
        ));

        return toResponse(lotRepository.save(newLot));
    }

    @Transactional
    public void handleTransactionFailure(TransactionClosed event) {
        AuctionLot lot = lotRepository.findById(event.auctionId())
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + event.auctionId()));

        if (lot.getStatus() != AuctionStatus.SOLD) {
            lot.setStatus(AuctionStatus.EXPIRED);
            log.info("Anúncio {} marcado como expirado após falha na transação de venda.", event.auctionId());
        } else {
            log.warn("Tentativa de reverter status do anúncio {} falhou pois o status não é SOLD.", event.auctionId());
        }
    }

    @Transactional
    public void processUserPenalty(UserStatusChanged event) {
        int BATCH_SIZE = 50;
        Pageable pageable = PageRequest.of(0, BATCH_SIZE);

        bidRepository.invalidateAllBidsFromUser(event.userId());

        while (true){
            List<AuctionLot> activeLots = lotRepository.findAllBySellerIdAndStatus(
                    event.userId(), AuctionStatus.ACTIVE, pageable);

            if (activeLots.isEmpty()) {
                break;
            }

            activeLots.forEach(lot -> {
                lot.setStatus(AuctionStatus.CANCELED);
                log.info("Status do anúncio {} alterado para CANCELED.", lot.getId());

                if (lot.getHighestBidderId() != null){
                    eventPublisher.publishEvent(new AuctionCanceled(
                            UUID.randomUUID(),
                            lot.getId(),
                            lot.getHighestBidderId(),
                            Instant.now()
                    ));
                }
            });

            lotRepository.saveAll(activeLots);
            lotRepository.flush();
        }

        while (true){
            List<AuctionLot> winningLots = lotRepository.findAllByHighestBidderIdAndStatus(event.userId(), AuctionStatus.ACTIVE, pageable);

            if (winningLots.isEmpty()) {
                break;
            }

            winningLots.forEach(lot -> bidRepository.findHighestValidBidForLot(lot.getId()).ifPresentOrElse(bid -> {
                lot.setHighestBidderId(bid.getBidderId());
                lot.setCurrentBidPrice(bid.getAmount());

                eventPublisher.publishEvent(new NewHighestBidderAssigned(
                        UUID.randomUUID(),
                        lot.getId(),
                        lot.getTitle(),
                        lot.getMainImageUrl(),
                        bid.getBidderId(),
                        bid.getId(),
                        bid.getAmount(),
                        Instant.now()
                ));
            }, () -> {
                lot.setHighestBidderId(null);
                lot.setCurrentBidPrice(lot.getInitialBidPrice());
            }));
            lotRepository.saveAll(winningLots);
            lotRepository.flush();
        }
    }

    private AuctionLotResponse toResponse(AuctionLot lot) {
        return new AuctionLotResponse(
                lot.getId(),
                lot.getSellerId(),
                lot.getTitle(),
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



