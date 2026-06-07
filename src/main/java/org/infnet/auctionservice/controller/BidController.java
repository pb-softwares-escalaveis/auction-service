package org.infnet.auctionservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.service.facade.BidFacade;
import org.infnet.auctionservice.dto.BidRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BidController {
    private final BidFacade bidFacade;

    @PostMapping("/auctions/{auctionId}/bids/place")
    public ResponseEntity<Void> placeBid(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("auctionId") Long auctionId,
            @Valid@RequestBody BidRequest request
    ) {
        bidFacade.placeBid(auctionId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
