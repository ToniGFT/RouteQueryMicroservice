package com.workshop.route.application;

import com.workshop.route.RouteApplication;
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

@SpringBootTest(classes = RouteApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class RouteServiceEndToEndTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RouteQueryRepository routeRepository;

    @BeforeEach
    void setupDatabase() {
        routeRepository.deleteAll().block();

        Route route1 = Route.builder().routeId(new ObjectId()).routeName("Route A").build();
        Route route2 = Route.builder().routeId(new ObjectId()).routeName("Route B").build();
        routeRepository.saveAll(List.of(route1, route2)).collectList().block();
    }

    @Test
    void getRouteById_EndToEnd_ShouldReturnExpectedRoute() {
        Route route = routeRepository.findAll().blockFirst();

        webTestClient.get()
                .uri("/routes/{idString}", route.getRouteId().toHexString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Route.class)
                .consumeWith(response -> {
                    Route routeResponse = response.getResponseBody();
                    assert routeResponse != null;
                    assertEquals(routeResponse.getRouteName(), "Route A");
                });
    }

    @Test
    void getAllRoutes_EndToEnd_ShouldReturnAllRoutes() {
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
                });
    }
}

