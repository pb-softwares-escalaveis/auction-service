package org.infnet.auctionservice.kafka;

import org.infnet.auctionservice.events.AuctionEvent;

public interface KafkaSenderInterface {
    void sendEvent(AuctionEvent event);
}
