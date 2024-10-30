package com.workshop.route.application.controller;

import com.workshop.route.application.response.service.RouteResponseService;
import com.workshop.route.application.services.RouteQueryService;
import com.workshop.route.domain.model.aggregates.Route;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/routes")
public class RouteController {

    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);
    private final RouteQueryService routeQueryService;
    private final RouteResponseService routeResponseService;

    public RouteController(RouteQueryService routeQueryService, RouteResponseService routeResponseService) {
        this.routeQueryService = routeQueryService;
        this.routeResponseService = routeResponseService;
    }

    @GetMapping("/{_id}")
    public Mono<ResponseEntity<Route>> getRouteById(@PathVariable("_id") ObjectId id) {
        logger.info("Fetching route with ID: {}", id);
        return routeQueryService.getRouteById(id)
                .flatMap(routeResponseService::buildOkResponse)
                .doOnSuccess(response -> logger.info("Successfully fetched route with ID: {}", id))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<ResponseEntity<Route>> getAllRoutes() {
        logger.info("Fetching all routes");
        return routeResponseService.buildRoutesResponse(routeQueryService.getAllRoutes())
                .doFirst(() -> logger.info("Starting to fetch all routes")) // Log al inicio de la operación
                .doOnComplete(() -> logger.info("Successfully fetched all routes"));
    }
}
