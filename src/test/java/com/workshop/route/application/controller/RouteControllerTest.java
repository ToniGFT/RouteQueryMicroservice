package com.workshop.route.application.controller;

import com.workshop.route.application.response.service.RouteResponseService;
import com.workshop.route.application.services.RouteQueryService;
import com.workshop.route.domain.model.aggregates.Route;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@DisplayName("RouteController Unit Tests")
class RouteControllerTest {

    @Mock
    private RouteQueryService routeQueryService;

    @Mock
    private RouteResponseService routeResponseService;

    @InjectMocks
    private RouteController routeController;

    private Route route;
    private ObjectId id;
    private String idString;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = new ObjectId("507f1f77bcf86cd799439011");
        idString = "507f1f77bcf86cd799439011";
        route = Route.builder()
                .routeId(id)
                .routeName("Test Route")
                .build();
    }

    @Test
    @DisplayName("Get Route By ID - Should Return OK Response")
    void getRouteById_shouldReturnOkResponse() {
        // given
        when(routeQueryService.getRouteById(id)).thenReturn(Mono.just(route));
        when(routeResponseService.buildOkResponse(route))
                .thenReturn(Mono.just(ResponseEntity.ok(route)));

        // when
        Mono<ResponseEntity<Route>> result = routeController.getRouteById(idString);

        // then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getBody() != null &&
                        response.getBody().getRouteId().toHexString().equals("507f1f77bcf86cd799439011"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Get All Routes - Should Return OK Response for Each Route")
    void getAllRoutes_shouldReturnOkResponse() {
        // given
        when(routeQueryService.getAllRoutes()).thenReturn(Flux.just(route));
        when(routeResponseService.buildRoutesResponse(route))
                .thenReturn(Flux.just(ResponseEntity.ok(route)));

        // when
        Flux<ResponseEntity<Route>> result = routeController.getAllRoutes();

        // then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getBody() != null &&
                        response.getBody().getRouteId().equals(id))
                .verifyComplete();
    }
}
