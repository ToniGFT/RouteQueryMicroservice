package com.workshop.route.domain.model.mapper;

import com.workshop.route.domain.events.RouteCreatedEvent;
import com.workshop.route.domain.events.RouteUpdatedEvent;
import com.workshop.route.domain.model.aggregates.Route;
import org.modelmapper.ModelMapper;

public class RouteEventMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();

    private RouteEventMapper() {
    }

    public static Route toRoute(RouteCreatedEvent event) {
        return modelMapper.map(event, Route.class);
    }

    public static Route toRoute(RouteUpdatedEvent event, Route existingRoute) {
        modelMapper.map(event, existingRoute);
        return existingRoute;
    }
}
