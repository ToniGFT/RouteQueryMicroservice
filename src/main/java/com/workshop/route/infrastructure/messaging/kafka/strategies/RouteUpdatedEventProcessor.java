package com.workshop.route.infrastructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.application.services.kafka.RouteEventProcessor;
import com.workshop.route.domain.events.RouteUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("ROUTE_UPDATED")
public class RouteUpdatedEventProcessor implements EventProcessor<RouteUpdatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RouteUpdatedEventProcessor.class);

    private final RouteEventProcessor routeEventProcessor;
    private final ObjectMapper objectMapper;

    public RouteUpdatedEventProcessor(RouteEventProcessor routeEventProcessor, ObjectMapper objectMapper) {
        this.routeEventProcessor = routeEventProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> process(String message) {
        logger.info("Starting to process ROUTE_UPDATED event. Message: {}", message);
        try {
            logger.debug("Deserializing message into RouteUpdatedEvent");
            RouteUpdatedEvent event = objectMapper.readValue(message, RouteUpdatedEvent.class);
            logger.info("Successfully deserialized RouteUpdatedEvent. Event: {}", event);

            return routeEventProcessor.processRouteUpdatedEvent(event)
                    .doOnSubscribe(subscription -> logger.info("Subscribed to RouteUpdatedEvent processing. Event: {}", event))
                    .doOnSuccess(result -> logger.info("Successfully processed RouteUpdatedEvent. Event: {}", event))
                    .doOnError(error -> logger.error("Error processing RouteUpdatedEvent. Event: {}, Error: {}", event, error.getMessage()))
                    .doFinally(signalType -> logger.info("Finished processing ROUTE_UPDATED event with signal: {}", signalType));
        } catch (Exception e) {
            logger.error("Failed to process RouteUpdatedEvent. Message: {}, Error: {}", message, e.getMessage(), e);
            return Mono.error(new RuntimeException("Failed to process RouteUpdatedEvent", e));
        }
    }
}
