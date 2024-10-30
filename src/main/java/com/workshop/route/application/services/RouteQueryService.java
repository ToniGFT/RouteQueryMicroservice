package com.workshop.route.application.services;

import com.workshop.route.domain.model.aggregates.Route;
import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RouteQueryService {
    Mono<Route> getRouteById(ObjectId id);

    Flux<Route> getAllRoutes();
}

