package com.workshop.route.application.services.kafka;

import com.workshop.route.domain.events.RouteCreatedEvent;
import com.workshop.route.domain.events.RouteDeletedEvent;
import com.workshop.route.domain.events.RouteUpdatedEvent;
import com.workshop.route.domain.model.aggregates.Route;
import com.workshop.route.domain.model.mapper.RouteEventMapper;
import com.workshop.route.infrastructure.repository.RouteCommandRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RouteEventProcessorImpl implements RouteEventProcessor {

    private final RouteCommandRepository routeCommandRepository;

    public RouteEventProcessorImpl(RouteCommandRepository routeCommandRepository) {
        this.routeCommandRepository = routeCommandRepository;
    }

    @Override
    public Mono<Void> processRouteCreatedEvent(RouteCreatedEvent event) {
        Route route = RouteEventMapper.toRoute(event);
        return routeCommandRepository.save(route).then();
    }

    @Override
    public Mono<Void> processRouteDeletedEvent(RouteDeletedEvent event) {
        return routeCommandRepository.deleteById(event.getRouteId()).then();
    }

    @Override
    public Mono<Void> processRouteUpdatedEvent(RouteUpdatedEvent event) {
        return routeCommandRepository.findById(event.getRouteId())
                .flatMap(existingRoute -> {
                    Route updatedRoute = RouteEventMapper.toRoute(event, existingRoute);
                    return routeCommandRepository.save(updatedRoute);
                })
                .then();
    }
}
