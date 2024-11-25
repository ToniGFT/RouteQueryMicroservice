package com.workshop.route.infrastructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.application.services.kafka.RouteEventProcessor;
import com.workshop.route.domain.events.RouteDeletedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("ROUTE_DELETED")
public class RouteDeletedEventProcessor implements EventProcessor<RouteDeletedEvent> {

    private final RouteEventProcessor routeEventProcessor;
    private final ObjectMapper objectMapper;

    public RouteDeletedEventProcessor(RouteEventProcessor routeEventProcessor, ObjectMapper objectMapper) {
        this.routeEventProcessor = routeEventProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> process(String message) {
        try {
            RouteDeletedEvent event = objectMapper.readValue(message, RouteDeletedEvent.class);
            return routeEventProcessor.processRouteDeletedEvent(event);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to process RouteDeletedEvent", e));
        }
    }
}
