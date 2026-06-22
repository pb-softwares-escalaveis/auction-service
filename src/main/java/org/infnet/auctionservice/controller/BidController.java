package org.infnet.auctionservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.dto.UserHeaderContext;
import org.infnet.auctionservice.dto.BidRequest;
import org.infnet.auctionservice.service.BidService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class BidController {
    private final BidService bidService;

    @PostMapping("/auctions/{id}/bids/place")
    public ResponseEntity<Void> placeBid(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Name") String userName,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Allowed") boolean isAllowed,
            @PathVariable("id") Long auctionId,
            @Valid@RequestBody BidRequest request
    ) {
        log.info("[BID CONTROLLER] Recebida requisição para registrar lance. auctionId={} userId={}", auctionId, userId);
        var ctx = new UserHeaderContext(userId, userName, userEmail, isAllowed);
        bidService.placeBid(auctionId, ctx, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
