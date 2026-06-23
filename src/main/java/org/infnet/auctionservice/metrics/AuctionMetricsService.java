package org.infnet.auctionservice.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.infnet.auctionservice.repository.AuctionLotRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionMetricsService {

    private final MeterRegistry meterRegistry;

    public void recordValidBid() {
        meterRegistry.counter("auction.bids.total", "status", "success").increment();
    }

    public void recordInvalidBid() {
        meterRegistry.counter("auction.bids.total", "status", "invalid").increment();
    }
}
