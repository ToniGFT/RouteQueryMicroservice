package com.workshop.route.application.response.service;

import com.workshop.route.domain.model.aggregates.Route;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("RouteResponseService Unit Tests")
class RouteResponseServiceTest {

    @InjectMocks
    private RouteResponseService routeResponseService;

    private Route route;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectId id = new ObjectId("507f1f77bcf86cd799439011");
        route = Route.builder()
                .routeId(id)
                .routeName("Test Route")
                .build();
    }

    @Test
    @DisplayName("Build OK Response - Should Return Mono with 200 OK")
    void buildOkResponse_shouldReturnOkResponse() {
        // given
        // when
        Mono<ResponseEntity<Route>> result = routeResponseService.buildOkResponse(route);

        // then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK &&
                        response.getBody() != null &&
                        response.getBody().getRouteId().toHexString().equals("507f1f77bcf86cd799439011"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Build Routes Response - Should Return Flux with 200 OK for Each Route")
    void buildRoutesResponse_shouldReturnOkResponsesForEachRoute() {
        // given
        Flux<Route> routeFlux = Flux.just(route);

        // when
        Flux<ResponseEntity<Route>> result = routeResponseService.buildRoutesResponse(route);

        // then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK &&
                        response.getBody() != null &&
                        response.getBody().getRouteId().toHexString().equals("507f1f77bcf86cd799439011"))
                .verifyComplete();
    }
}
