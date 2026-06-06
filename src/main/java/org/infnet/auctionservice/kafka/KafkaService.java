package org.infnet.auctionservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.auctionservice.events.AuctionEvent;
import org.infnet.auctionservice.events.lots.*;
import org.infnet.auctionservice.events.bids.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService implements KafkaSenderInterface {
    @Value("${app.kafka-topics.auction-created-pending}")
    String PENDING_TOPIC;
    @Value("${app.kafka-topics.auction-approved}")
    String APPROVED_TOPIC;
    @Value("${app.kafka-topics.auction-rejected}")
    String REJECTED_TOPIC;
    @Value("${app.kafka-topics.auction-ended-without-winner}")
    String ENDED_WITHOUT_WINNER_TOPIC;
    @Value("${app.kafka-topics.auction-ended-with-winner}")
    String ENDED_WITH_WINNER_TOPIC;
    @Value("${app.kafka-topics.bid-placed}")
    String BID_PLACED_TOPIC;
    @Value("${app.kafka-topics.auction-clicked}")
    String LOT_CLICKED_TOPIC;
    @Value("${app.kafka-topics.auction-removed}")
    String REMOVED_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(AuctionEvent event) {
        String kafkaKey = event.auctionId().toString();

        String topic = switch (event) {
            case AuctionApproved ignored -> APPROVED_TOPIC;
            case AuctionCreatedPendingReview ignored -> PENDING_TOPIC;
            case AuctionRejected ignored -> REJECTED_TOPIC;
            case AuctionEndedWithoutWinner ignored -> ENDED_WITHOUT_WINNER_TOPIC;
            case AuctionEndedWithWinner ignored -> ENDED_WITH_WINNER_TOPIC;
            case AuctionClicked ignored -> LOT_CLICKED_TOPIC;
            case BidPlaced ignored -> BID_PLACED_TOPIC;
            case AuctionRemoved ignored -> REMOVED_TOPIC;

            default -> throw new IllegalArgumentException("Evento não mapeado para envio: " + event.getClass().getSimpleName());
        };

        kafkaTemplate.send(topic, kafkaKey, event);
    }
}
