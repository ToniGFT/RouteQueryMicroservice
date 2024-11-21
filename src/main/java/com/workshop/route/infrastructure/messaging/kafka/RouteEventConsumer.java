package com.workshop.route.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.infrastructure.messaging.kafka.strategies.EventProcessorRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class RouteEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RouteEventConsumer.class);

    private final ReceiverOptions<String, String> receiverOptions;
    private final EventProcessorRegistry eventProcessorRegistry;
    private final ObjectMapper objectMapper;

    public RouteEventConsumer(ReceiverOptions<String, String> receiverOptions,
                              EventProcessorRegistry eventProcessorRegistry,
                              ObjectMapper objectMapper) {
        this.receiverOptions = receiverOptions;
        this.eventProcessorRegistry = eventProcessorRegistry;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void consumeEvents() {
        KafkaReceiver.create(receiverOptions)
                .receive()
                .flatMap(record -> {
                    String message = record.value();
                    try {
                        String eventType = extractEventType(message);
                        return eventProcessorRegistry.process(eventType, message)
                                .doOnSuccess(success -> record.receiverOffset().acknowledge())
                                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5)));
                    } catch (Exception e) {
                        logger.error("Failed to process record: {}", e.getMessage());
                        record.receiverOffset().acknowledge();
                        return Mono.empty();
                    }
                })
                .doOnError(e -> logger.error("Global error in Kafka consumer: {}", e.getMessage(), e))
                .onErrorContinue((throwable, object) -> {
                    logger.error("Skipping record due to error: {}", throwable.getMessage());
                })
                .subscribe();
    }

    private String extractEventType(String message) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(message);
        if (!node.has("type")) {
            throw new IllegalArgumentException("Message does not contain 'type' field");
        }
        return node.get("type").asText();
    }
}
