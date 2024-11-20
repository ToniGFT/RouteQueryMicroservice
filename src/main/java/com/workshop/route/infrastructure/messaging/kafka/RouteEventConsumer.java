package com.workshop.route.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.infrastructure.messaging.kafka.strategies.EventProcessorRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

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
                        return eventProcessorRegistry.process(eventType, message);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Failed to process record", e));
                    }
                })
                .onErrorContinue((e, o) -> {
                    logger.error("Error processing record: {}", e.getMessage(), e);
                })
                .subscribe();
    }

    private String extractEventType(String message) throws JsonProcessingException {
        return objectMapper.readTree(message).get("type").asText();
    }
}
