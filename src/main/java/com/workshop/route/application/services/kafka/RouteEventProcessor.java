package com.workshop.route.application.services.kafka;

import com.workshop.route.domain.events.RouteCreatedEvent;
import com.workshop.route.domain.events.RouteDeletedEvent;
import com.workshop.route.domain.events.RouteUpdatedEvent;
import reactor.core.publisher.Mono;

public interface RouteEventProcessor {
    Mono<Void> processRouteCreatedEvent(RouteCreatedEvent event);

    Mono<Void> processRouteDeletedEvent(RouteDeletedEvent event);

    Mono<Void> processRouteUpdatedEvent(RouteUpdatedEvent event);
}
