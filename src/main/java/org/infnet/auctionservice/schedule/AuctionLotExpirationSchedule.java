package org.infnet.auctionservice.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.service.AuctionLotExpirationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionLotExpirationSchedule {
    private final AuctionLotRepository lotRepository;
    private final AuctionLotExpirationService expirationService;

    @Scheduled(cron = "0 */1 * * * *")
    public void executeExpire() {
        int BATCH_SIZE = 100;
        int totalProcessed = 0;

        while (true) {
            List<Long> ids = lotRepository.findAllExpiredIds(
                    Instant.now(),
                    PageRequest.of(0, BATCH_SIZE)
            );

            if (ids.isEmpty()) {
                if (totalProcessed > 0) {
                    log.info("Schedule concluído: {} anúncios expirados foram processados.", totalProcessed);
                }
                log.info("Schedule concluído, nenhum anúncio expirado encontrado.");
                break;
            }

            for (Long id : ids) {
                try {
                    expirationService.processEnd(id);
                    totalProcessed++;
                } catch (Exception e) {
                    log.error("Erro ao processar anúncio expirado: {}", id, e);
                }
            }
        }
    }
}
