package com.workshop.route.infrastructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.application.services.kafka.RouteEventProcessor;
import com.workshop.route.domain.events.RouteUpdatedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("ROUTE_UPDATED")
public class RouteUpdatedEventProcessor implements EventProcessor<RouteUpdatedEvent> {

    private final RouteEventProcessor routeEventProcessor;
    private final ObjectMapper objectMapper;

    public RouteUpdatedEventProcessor(RouteEventProcessor routeEventProcessor, ObjectMapper objectMapper) {
        this.routeEventProcessor = routeEventProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> process(String message) {
        try {
            RouteUpdatedEvent event = objectMapper.readValue(message, RouteUpdatedEvent.class);
            return routeEventProcessor.processRouteUpdatedEvent(event);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to process RouteUpdatedEvent", e));
        }
    }
}
