package org.infnet.auctionservice.schedule;

import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.service.AuctionLotExpirationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuctionLotExpirationSchedule {
    private final AuctionLotRepository lotRepository;
    private final AuctionLotExpirationService expirationService;

    @Scheduled(cron = "0 */1 * * * *")
    public void executeExpire() {
        int BATCH_SIZE = 100;
        List<Long> ids = lotRepository.findAllExpiredIds(
                Instant.now(),
                PageRequest.of(0, BATCH_SIZE)
        );

        if (ids.isEmpty()) {
            System.out.println("NOTHING TO EXPIRE"); // LOG
            return;
        }

        for (Long id : ids) {
            try {
                expirationService.processEnd(id);
            } catch (Exception e) {
                System.out.println("Error processing expired lot: " + id); // LOG
            }
        }
    }
}
