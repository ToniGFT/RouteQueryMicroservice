package com.workshop.route.application.response.builder;

import com.workshop.route.domain.model.aggregates.Route;
import org.springframework.http.ResponseEntity;

public class RouteResponseBuilder {

    public static ResponseEntity<Route> generateOkResponse(Route route) {
        return ResponseEntity.ok(route);
    }

}
