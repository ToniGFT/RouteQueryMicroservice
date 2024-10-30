package com.workshop.route.application.services;

import com.workshop.route.domain.model.aggregates.Route;
import com.workshop.route.domain.repository.RouteQueryRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RouteQueryServiceImpl implements RouteQueryService {

    private final RouteQueryRepository routeQueryRepository;

    public RouteQueryServiceImpl(RouteQueryRepository routeQueryRepository) {
        this.routeQueryRepository = routeQueryRepository;
    }

    @Override
    public Mono<Route> getRouteById(ObjectId id) {
        return routeQueryRepository.findById(id);
    }

    @Override
    public Flux<Route> getAllRoutes() {
        return routeQueryRepository.findAll();
    }
}
