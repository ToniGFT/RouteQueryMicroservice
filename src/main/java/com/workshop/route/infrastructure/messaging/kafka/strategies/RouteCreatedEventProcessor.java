package com.workshop.route.infrastructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.application.services.kafka.RouteEventProcessor;
import com.workshop.route.domain.events.RouteCreatedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("ROUTE_CREATED")
public class RouteCreatedEventProcessor implements EventProcessor<RouteCreatedEvent> {

    private final RouteEventProcessor routeEventProcessor;
    private final ObjectMapper objectMapper;

    public RouteCreatedEventProcessor(RouteEventProcessor routeEventProcessor, ObjectMapper objectMapper) {
        this.routeEventProcessor = routeEventProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> process(String message) {
        try {
            RouteCreatedEvent event = objectMapper.readValue(message, RouteCreatedEvent.class);
            return routeEventProcessor.processRouteCreatedEvent(event);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to process RouteCreatedEvent", e));
        }
    }
}
