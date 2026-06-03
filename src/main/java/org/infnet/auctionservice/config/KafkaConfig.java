package org.infnet.auctionservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
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

    @Bean
    public NewTopic pendingTopic() {
        return TopicBuilder.name(PENDING_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic approvedTopic() {
        return TopicBuilder.name(APPROVED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic rejectedTopic() {
        return TopicBuilder.name(REJECTED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic endedWithoutWinnerTopic() {
        return TopicBuilder.name(ENDED_WITHOUT_WINNER_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic endedWithWinnerTopic() {
        return TopicBuilder.name(ENDED_WITH_WINNER_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bidPlacedTopic() {
        return TopicBuilder.name(BID_PLACED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic lotClickedTopic() {
        return TopicBuilder.name(LOT_CLICKED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    //topicos do review-service pra testar o listener
    @Bean
    public NewTopic lotRejectedTopic() {
        return TopicBuilder.name("reviews.auction.rejected")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic lotApprovedTopic() {
        return TopicBuilder.name("reviews.auction.approved")
                .partitions(3)
                .replicas(1)
                .build();
    }


}
