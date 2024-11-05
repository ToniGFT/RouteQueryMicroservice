package com.workshop.route.application.controller;

import com.workshop.route.application.response.service.RouteResponseService;
import com.workshop.route.application.services.RouteQueryService;
import com.workshop.route.domain.model.aggregates.Route;
import com.workshop.route.domain.repository.RouteQueryRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class RouteControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RouteQueryRepository routeRepository;

    @Autowired
    private RouteQueryService routeQueryService;

    @Autowired
    private RouteResponseService routeResponseService;

    @BeforeEach
    void setUp() {

        routeRepository.deleteAll().block();

        Route route1 = Route.builder().routeId(new ObjectId()).routeName("Route 1").build();
        Route route2 = Route.builder().routeId(new ObjectId()).routeName("Route 2").build();
        routeRepository.saveAll(List.of(route1, route2)).collectList().block();
    }

    @Test
    void getRouteById_ShouldReturnRoute_WhenRouteExists() {

        Route testRoute = Route.builder()
                .routeId(new ObjectId())
                .routeName("Test Route")
                .build();

        routeRepository.save(testRoute).block();

        webTestClient.get()
                .uri("/routes/{idString}", testRoute.getRouteId().toHexString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Route.class)
                .consumeWith(response -> {
                    Route routeResponse = response.getResponseBody();
                    assert routeResponse != null;
                    assertEquals(routeResponse.getRouteName(), testRoute.getRouteName());
                });
    }


    @Test
    void getRouteById_ShouldReturnNotFound_WhenRouteDoesNotExist() {
        ObjectId nonExistentRouteId = new ObjectId();

        webTestClient.get()
                .uri("/routes/{idString}", nonExistentRouteId.toHexString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAllRoutes_ShouldReturnAllRoutes() {
        webTestClient.get()
                .uri("/routes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .consumeWith(response -> {
                    List<?> responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assertEquals(responseBody.size(), 2);

                    Map<?, ?> route1Map = (Map<?, ?>) ((Map<?, ?>) responseBody.get(0)).get("body");
                    Map<?, ?> route2Map = (Map<?, ?>) ((Map<?, ?>) responseBody.get(1)).get("body");

                    assertEquals(route1Map.get("routeName"), "Route 1");
                    assertEquals(route2Map.get("routeName"), "Route 2");
                });
    }


}
