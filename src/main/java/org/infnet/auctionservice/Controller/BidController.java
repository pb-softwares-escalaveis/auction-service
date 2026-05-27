package org.infnet.auctionservice.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.Service.BidService;
import org.infnet.auctionservice.dto.BidRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BidController {
    private final BidService bidService;

    @PostMapping("/auctions/{auctionId}/bids/place")
    public ResponseEntity<Void> placeBid(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("auctionId") Long auctionId,
            @Valid@RequestBody BidRequest request
    ) {
        bidService.placeBid(auctionId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
