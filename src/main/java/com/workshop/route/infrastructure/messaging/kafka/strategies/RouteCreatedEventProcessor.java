package com.workshop.route.infrastructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.application.services.kafka.RouteEventProcessor;
import com.workshop.route.domain.events.RouteCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("ROUTE_CREATED")
public class RouteCreatedEventProcessor implements EventProcessor<RouteCreatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RouteCreatedEventProcessor.class);

    private final RouteEventProcessor routeEventProcessor;
    private final ObjectMapper objectMapper;

    public RouteCreatedEventProcessor(RouteEventProcessor routeEventProcessor, ObjectMapper objectMapper) {
        this.routeEventProcessor = routeEventProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> process(String message) {
        logger.info("Starting to process ROUTE_CREATED event. Message: {}", message);
        try {
            logger.debug("Deserializing message into RouteCreatedEvent");
            RouteCreatedEvent event = objectMapper.readValue(message, RouteCreatedEvent.class);
            logger.info("Successfully deserialized RouteCreatedEvent. Event: {}", event);

            return routeEventProcessor.processRouteCreatedEvent(event)
                    .doOnSubscribe(subscription -> logger.info("Subscribed to RouteCreatedEvent processing. Event: {}", event))
                    .doOnSuccess(result -> logger.info("Successfully processed RouteCreatedEvent. Event: {}", event))
                    .doOnError(error -> logger.error("Error processing RouteCreatedEvent. Event: {}, Error: {}", event, error.getMessage()))
                    .doFinally(signalType -> logger.info("Finished processing ROUTE_CREATED event with signal: {}", signalType));
        } catch (Exception e) {
            logger.error("Failed to process RouteCreatedEvent. Message: {}, Error: {}", message, e.getMessage(), e);
            return Mono.error(new RuntimeException("Failed to process RouteCreatedEvent", e));
        }
    }
}
