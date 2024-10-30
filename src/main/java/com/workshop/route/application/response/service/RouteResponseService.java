package com.workshop.route.application.response.service;

import com.workshop.route.application.response.builder.RouteResponseBuilder;
import com.workshop.route.domain.model.aggregates.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RouteResponseService {

    public Mono<ResponseEntity<Route>> buildOkResponse(Route route) {
        return Mono.fromCallable(() -> RouteResponseBuilder.generateOkResponse(route));
    }

    public Flux<ResponseEntity<Route>> buildRoutesResponse(Flux<Route> routes) {
        return routes.map(RouteResponseBuilder::generateOkResponse);
    }
}
