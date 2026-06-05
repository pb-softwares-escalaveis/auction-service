package org.infnet.auctionservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.service.AuctionLotFacade;
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
@CrossOrigin(origins = "*")
public class AuctionLotController {
    private final AuctionLotService lotService;
    private final AuctionLotFacade lotFacade;

    @GetMapping("/")
    public ResponseEntity<Page<AuctionLotResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10")@Max(25) int size
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lotService.listAllActiveAuctionLots(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionLotResponse> getAuctionLot(
            @PathVariable("id") Long id
    ){
        return ResponseEntity.status(HttpStatus.OK).body(lotService.getAuctionLot(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuctionLot(
            @PathVariable("id") Long lotId,
            @RequestHeader("X-User-Id") UUID userId

    ){
        lotService.deleteAuctionLot(userId, lotId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<AuctionLotResponse> createAuctionLot(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid@RequestPart("data") AuctionLotRequest request,
            @NotNull@RequestPart("image") MultipartFile image
    ) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(lotFacade.createAuctionLot(userId, request, image));
    }
}
