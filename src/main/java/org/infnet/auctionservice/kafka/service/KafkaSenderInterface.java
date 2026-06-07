package org.infnet.auctionservice.kafka.service;

import org.infnet.auctionservice.events.AuctionEvent;

public interface KafkaSenderInterface {
    void sendEvent(AuctionEvent event);
}
