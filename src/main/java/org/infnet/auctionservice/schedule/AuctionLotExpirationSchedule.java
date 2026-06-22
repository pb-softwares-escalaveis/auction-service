package org.infnet.auctionservice.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.infnet.auctionservice.service.AuctionLotExpirationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.infnet.auctionservice.utils.CorrelationIdUtil;
import org.slf4j.MDC;

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
                    log.info("[AUCTION LOT EXPIRATION SCHEDULE] Schedule concluído. processedCount={}", totalProcessed);
                } else {
                    log.info("[AUCTION LOT EXPIRATION SCHEDULE] Schedule concluído. processedCount=0");
                }
                break;
            }

            for (Long id : ids) {
                try {
                    String correlationId = CorrelationIdUtil.generateCorrelationId();
                    MDC.put("correlationId", correlationId);
                    
                    expirationService.processEnd(id);
                    totalProcessed++;
                } catch (Exception e) {
                    log.error("[AUCTION LOT EXPIRATION SCHEDULE] Erro ao processar anúncio expirado. auctionId={}", id, e);
                } finally {
                    CorrelationIdUtil.clear();
                }
            }
        }
    }
}
