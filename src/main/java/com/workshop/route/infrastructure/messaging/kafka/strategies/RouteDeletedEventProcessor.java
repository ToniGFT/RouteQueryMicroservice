package com.workshop.route.infrastructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.application.services.kafka.RouteEventProcessor;
import com.workshop.route.domain.events.RouteDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("ROUTE_DELETED")
public class RouteDeletedEventProcessor implements EventProcessor<RouteDeletedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RouteDeletedEventProcessor.class);

    private final RouteEventProcessor routeEventProcessor;
    private final ObjectMapper objectMapper;

    public RouteDeletedEventProcessor(RouteEventProcessor routeEventProcessor, ObjectMapper objectMapper) {
        this.routeEventProcessor = routeEventProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> process(String message) {
        logger.info("Starting to process ROUTE_DELETED event. Message: {}", message);
        try {
            logger.debug("Deserializing message into RouteDeletedEvent");
            RouteDeletedEvent event = objectMapper.readValue(message, RouteDeletedEvent.class);
            logger.info("Successfully deserialized RouteDeletedEvent. Event: {}", event);

            return routeEventProcessor.processRouteDeletedEvent(event)
                    .doOnSubscribe(subscription -> logger.info("Subscribed to RouteDeletedEvent processing. Event: {}", event))
                    .doOnSuccess(result -> logger.info("Successfully processed RouteDeletedEvent. Event: {}", event))
                    .doOnError(error -> logger.error("Error processing RouteDeletedEvent. Event: {}, Error: {}", event, error.getMessage()))
                    .doFinally(signalType -> logger.info("Finished processing ROUTE_DELETED event with signal: {}", signalType));
        } catch (Exception e) {
            logger.error("Failed to process RouteDeletedEvent. Message: {}, Error: {}", message, e.getMessage(), e);
            return Mono.error(new RuntimeException("Failed to process RouteDeletedEvent", e));
        }
    }
}
