package org.infnet.auctionservice.kafka.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.infnet.auctionservice.events.AuctionEvent;
import org.infnet.auctionservice.utils.CorrelationIdUtil;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCorrelationIdInterceptor implements RecordInterceptor<Object, Object> {

    private final ObjectMapper objectMapper;
    private static final String CORRELATION_ID_MDC = "correlationId";

    @Override
    public ConsumerRecord<Object, Object> intercept(ConsumerRecord<Object, Object> record, @NonNull Consumer<Object, Object> consumer) {
        String correlationId = null;

        if (record.value() instanceof String jsonPayload) {
            try {
                JsonNode rootNode = objectMapper.readTree(jsonPayload);
                if (rootNode.has("correlationd") && !rootNode.get("correlationId").isNull()) {
                    correlationId = rootNode.get("correlationId").asText();
                }
            } catch (Exception e) {
                log.warn("[KAFKA CORRELATION INTERCEPTOR] Falha ao tentar extrair correlationId do payload JSON. topic={} error={}", record.topic(), e.getMessage());
            }
        } else if (record.value() instanceof AuctionEvent event) {
            if (event.correlationId() != null) {
                correlationId = event.correlationId().toString();
            }
        }

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = CorrelationIdUtil.generateCorrelationId();
            log.debug("[KAFKA CORRELATION INTERCEPTOR] CorrelationId gerado internamente para evento consumido. topic={} correlationId={}", record.topic(), correlationId);
        } else {
            log.debug("[KAFKA CORRELATION INTERCEPTOR] CorrelationId extraido do evento consumido. topic={} correlationId={}", record.topic(), correlationId);
        }

        MDC.put(CORRELATION_ID_MDC, correlationId);
        
        return record;
    }

    @Override
    public void success(@NonNull ConsumerRecord<Object, Object> record, @NonNull Consumer<Object, Object> consumer) {
        CorrelationIdUtil.clear();
    }

    @Override
    public void failure(@NonNull ConsumerRecord<Object, Object> record, @NonNull Exception exception, @NonNull Consumer<Object, Object> consumer) {
        CorrelationIdUtil.clear();
    }
}
