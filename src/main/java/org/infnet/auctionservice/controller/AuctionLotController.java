package org.infnet.auctionservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.dto.AuctionLotWithSellerInfo;
import org.infnet.auctionservice.dto.UserHeaderContext;
import org.infnet.auctionservice.enums.AuctionStatus;
import org.infnet.auctionservice.service.facade.AuctionLotFacade;
import org.infnet.auctionservice.service.AuctionLotService;
import org.infnet.auctionservice.dto.AuctionLotRequest;
import org.infnet.auctionservice.dto.AuctionLotResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
@Slf4j
public class AuctionLotController {
    private final AuctionLotService lotService;
    private final AuctionLotFacade lotFacade;

    // RESPONSABILIDADE DO LISTING SERVICE, REMOVER DEPOIS
    @GetMapping("/")
    public ResponseEntity<Page<AuctionLotResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10")@Max(25) int size
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lotService.listAllActiveAuctionLots(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionLotWithSellerInfo> getAuctionLotWithSellerInfo(
            @PathVariable("id") Long auctionLotId,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(lotService.getFullAuctionLot(auctionLotId, userId));
    }

    @PostMapping("/")
    public ResponseEntity<Page<AuctionLotResponse>> getUserAuctionLotsByOptionalStatus(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam("status") AuctionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20")@Max(25) int size
    ){
        return ResponseEntity.status(HttpStatus.OK).body(lotService.listAllUserLotsByStatus(userId, status, page, size));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAuctionLot(
            @PathVariable("id") Long lotId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Allowed") boolean isAllowed

    ){
        log.info("[AUCTION LOT CONTROLLER] Recebida requisição para remover anúncio. auctionId={} userId={}", lotId, userId);
        var userCtx = new UserHeaderContext(userId, userName, userEmail, isAllowed);
        lotService.removeAuctionLot(userCtx, lotId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<AuctionLotResponse> createAuctionLot(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Allowed") boolean isAllowed,
            @Valid@RequestPart("data") AuctionLotRequest request,
            @NotNull@RequestPart("image") MultipartFile image
    ) throws Exception {
        log.info("[AUCTION LOT CONTROLLER] Recebida requisição para criar anúncio. userId={}", userId);
        var ctx = new UserHeaderContext(userId, userName, userEmail, isAllowed);
        return ResponseEntity.status(HttpStatus.CREATED).body(lotFacade.createAuctionLot(ctx, request, image));
    }

    @PostMapping(value = "/{id}/renew")
    public ResponseEntity<AuctionLotResponse> renewAuctionLot(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Allowed") boolean isAllowed,
            @PathParam("id") Long lotId
    ){
        log.info("[AUCTION LOT CONTROLLER] Recebida requisição para renovar anúncio. auctionId={} userId={}", lotId, userId);
        var ctx = new UserHeaderContext(userId, userName, userEmail, isAllowed);

        return ResponseEntity.status(HttpStatus.OK).body(lotService.renewAuctionLot(lotId, ctx));
    }
}
