package com.workshop.route.application.services.kafka;

import com.workshop.route.domain.events.RouteCreatedEvent;
import com.workshop.route.domain.events.RouteDeletedEvent;
import com.workshop.route.domain.events.RouteUpdatedEvent;
import com.workshop.route.domain.model.aggregates.Route;
import com.workshop.route.domain.model.mapper.RouteEventMapper;
import com.workshop.route.infrastructure.repository.RouteCommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RouteEventProcessorImpl implements RouteEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RouteEventProcessorImpl.class);

    private final RouteCommandRepository routeCommandRepository;

    public RouteEventProcessorImpl(RouteCommandRepository routeCommandRepository) {
        this.routeCommandRepository = routeCommandRepository;
    }

    @Override
    public Mono<Void> processRouteCreatedEvent(RouteCreatedEvent event) {
        logger.info("Processing RouteCreatedEvent: {}", event);
        Route route = RouteEventMapper.toRoute(event);
        logger.debug("Mapped RouteCreatedEvent to Route: {}", route);

        return routeCommandRepository.save(route)
                .doOnSuccess(savedRoute -> logger.info("Successfully saved route to database: {}", savedRoute))
                .doOnError(error -> logger.error("Failed to save route to database: {}", error.getMessage(), error))
                .then();
    }

    @Override
    public Mono<Void> processRouteDeletedEvent(RouteDeletedEvent event) {
        logger.info("Processing RouteDeletedEvent: {}", event);
        return routeCommandRepository.deleteById(event.getRouteId())
                .doOnSuccess(unused -> logger.info("Successfully deleted route with ID: {}", event.getRouteId()))
                .doOnError(error -> logger.error("Failed to delete route with ID: {}", event.getRouteId(), error))
                .then();
    }

    @Override
    public Mono<Void> processRouteUpdatedEvent(RouteUpdatedEvent event) {
        logger.info("Processing RouteUpdatedEvent: {}", event);
        return routeCommandRepository.findById(event.getRouteId())
                .doOnSubscribe(subscription -> logger.debug("Fetching route with ID: {}", event.getRouteId()))
                .doOnNext(existingRoute -> logger.debug("Found existing route: {}", existingRoute))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No route found with ID: {}", event.getRouteId());
                    return Mono.empty();
                }))
                .flatMap(existingRoute -> {
                    Route updatedRoute = RouteEventMapper.toRoute(event, existingRoute);
                    logger.debug("Mapped RouteUpdatedEvent to updated Route: {}", updatedRoute);
                    return routeCommandRepository.save(updatedRoute)
                            .doOnSuccess(savedRoute -> logger.info("Successfully updated route in database: {}", savedRoute));
                })
                .doOnError(error -> logger.error("Failed to update route with ID: {}", event.getRouteId(), error))
                .then();
    }
}
