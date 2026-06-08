package org.infnet.auctionservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.domain.AuctionLot;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.dto.AuctionLotRequest;
import org.infnet.auctionservice.dto.AuctionLotResponse;
import org.infnet.auctionservice.events.lots.*;
import org.infnet.auctionservice.events.review.AuctionReviewApproved;
import org.infnet.auctionservice.events.review.AuctionReviewRejected;
import org.infnet.auctionservice.exception.UserNotAllowedException;
import org.infnet.auctionservice.dto.UserStatusResponse;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.storage.BucketStorageService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionLotService {
    private final AuctionLotRepository lotRepository;
    private final BucketStorageService bucketService;
    private final ApplicationEventPublisher eventPublisher;

    public AuctionLotResponse getAuctionLot(Long lotId) {
        AuctionLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        eventPublisher.publishEvent(new AuctionClicked(
                lot.getId(),
                lot.getCurrentBidPrice(),
                lot.getCategory(),
                ZonedDateTime.now().toInstant(),
                UUID.randomUUID()
        ));
        return toResponse(lot);
    }

    //responsabilidade do listing-service - REMOVER DEPOIS
    public Page<AuctionLotResponse> listAllActiveAuctionLots(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lotRepository.findByStatusEquals(AuctionStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public AuctionLotResponse registerAuctionLot(AuctionLotRequest dto, String imageUrl, UserStatusResponse user) {
        AuctionLot lot = new AuctionLot(
                user.id(),
                user.name(),
                user.email(),
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
    public void removeAuctionLot(UserStatusResponse user, Long lotId) {
        AuctionLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + lotId));

        if (!lot.getSellerId().equals(user.id())) {
            throw new UserNotAllowedException("Usuário não autorizado a deletar este anúncio.");
        }

        lot.setStatus(AuctionStatus.REMOVED);
        lotRepository.save(lot);

        eventPublisher.publishEvent(new AuctionRemoved(
                lot.getId(),
                lot.getSellerId(),
                user.name(),
                user.email(),
                lot.getTitle(),
                lot.getMainImageUrl(),
                Instant.now(),
                UUID.randomUUID()
        ));
    }

    @Transactional
    public void approveAuctionLot(AuctionReviewApproved event, UserStatusResponse user) {
        AuctionLot lot = lotRepository.findById(event.auctionId())
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + event.auctionId()));

        lot.setStatus(AuctionStatus.ACTIVE);
        lot.setExpirationDate(Instant.now().plus(lot.getDurationInDays(), ChronoUnit.DAYS));
        lotRepository.save(lot);

        eventPublisher.publishEvent(new AuctionApproved(
                lot.getId(),
                lot.getSellerId(),
                user.name(),
                user.email(),
                lot.getTitle(),
                lot.getMainImageUrl(),
                lot.getCreatedAt(),
                Instant.now(),
                UUID.randomUUID()
        ));
    }

    @Transactional
    public void rejectAuctionLot(AuctionReviewRejected event, UserStatusResponse user) {
        AuctionLot lot = lotRepository.findById(event.auctionId())
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado com id: " + event.auctionId()));

        lot.setStatus(AuctionStatus.REJECTED);
        lot.setExpirationDate(Instant.now().minus(lot.getDurationInDays(), ChronoUnit.DAYS));
        lotRepository.save(lot);

        eventPublisher.publishEvent(new AuctionRejected(
                lot.getId(),
                lot.getSellerId(),
                user.name(),
                user.email(),
                event.reason(),
                lot.getTitle(),
                lot.getMainImageUrl(),
                Instant.now(),
                UUID.randomUUID()
        ));
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

