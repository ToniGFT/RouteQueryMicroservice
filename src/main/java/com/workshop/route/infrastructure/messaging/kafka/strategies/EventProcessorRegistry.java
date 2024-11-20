package com.workshop.route.infrastructure.messaging.kafka.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventProcessorRegistry {

    private static final Logger logger = LoggerFactory.getLogger(EventProcessorRegistry.class);

    private final Map<String, EventProcessor<?>> processors;

    public EventProcessorRegistry(List<EventProcessor<?>> processorList) {
        this.processors = processorList.stream()
                .collect(Collectors.toMap(
                        processor -> processor.getClass().getAnnotation(Component.class).value(),
                        Function.identity()
                ));
    }

    public Mono<Void> process(String type, String message) {
        EventProcessor<?> processor = processors.get(type);
        if (processor == null) {
            logger.warn("Unknown event type: {}", type);
            return Mono.empty();
        }
        return processor.process(message);
    }
}
