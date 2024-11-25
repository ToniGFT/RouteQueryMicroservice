package com.workshop.route.domain.events;

import com.workshop.route.domain.model.entities.Schedule;
import com.workshop.route.domain.model.entities.Stop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteUpdatedEvent {
    private ObjectId routeId;
    private String routeName;
    private List<Stop> stops;
    private Schedule schedule;
}
