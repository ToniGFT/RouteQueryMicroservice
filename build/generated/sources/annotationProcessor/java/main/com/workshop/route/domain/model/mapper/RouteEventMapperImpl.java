package com.workshop.route.domain.model.mapper;

import com.workshop.route.domain.events.RouteCreatedEvent;
import com.workshop.route.domain.events.RouteUpdatedEvent;
import com.workshop.route.domain.model.aggregates.Route;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-25T11:03:31+0100",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class RouteEventMapperImpl implements RouteEventMapper {

    @Override
    public Route toRoute(RouteCreatedEvent event) {
        if ( event == null ) {
            return null;
        }

        Route route = new Route();

        return route;
    }

    @Override
    public void updateRouteFromEvent(RouteUpdatedEvent event, Route existingRoute) {
        if ( event == null ) {
            return;
        }
    }
}
