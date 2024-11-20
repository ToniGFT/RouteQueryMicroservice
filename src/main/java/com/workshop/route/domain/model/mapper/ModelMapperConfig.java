package com.workshop.route.domain.model.mapper;

import com.workshop.route.domain.events.RouteUpdatedEvent;
import com.workshop.route.domain.model.aggregates.Route;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class ModelMapperConfig {

    @Getter
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addMappings(new PropertyMap<RouteUpdatedEvent, Route>() {
            @Override
            protected void configure() {
                skip(destination.getRouteId());
            }
        });
    }

    private ModelMapperConfig() {
    }

}
