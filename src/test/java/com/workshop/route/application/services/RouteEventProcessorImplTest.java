package com.workshop.route.application.services.kafka;

import com.workshop.route.domain.events.RouteCreatedEvent;
import com.workshop.route.domain.events.RouteDeletedEvent;
import com.workshop.route.domain.events.RouteUpdatedEvent;
import com.workshop.route.domain.model.aggregates.Route;
import com.workshop.route.domain.model.entities.Schedule;
import com.workshop.route.domain.model.entities.Stop;
import com.workshop.route.domain.model.valueobjects.Coordinates;
import com.workshop.route.domain.model.valueobjects.WeekSchedule;
import com.workshop.route.infrastructure.repository.RouteCommandRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;

@DisplayName("RouteEventProcessorImpl Unit Tests")
class RouteEventProcessorImplTest {

    @Mock
    private RouteCommandRepository routeCommandRepository;

    @InjectMocks
    private RouteEventProcessorImpl routeEventProcessor;

    private RouteCreatedEvent createdEvent;
    private RouteDeletedEvent deletedEvent;
    private RouteUpdatedEvent updatedEvent;
    private Route route;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Stop stop1 = Stop.builder()
                .stopId("1")
                .stopName("Central Station")
                .coordinates(new Coordinates(40.7128, -74.0060))
                .arrivalTimes(List.of("08:00", "08:30", "09:00"))
                .build();

        Stop stop2 = Stop.builder()
                .stopId("2")
                .stopName("North Plaza")
                .coordinates(new Coordinates(40.7306, -73.9352))
                .arrivalTimes(List.of("08:15", "08:45", "09:15"))
                .build();

        List<Stop> stops = List.of(stop1, stop2);

        WeekSchedule weekdays = WeekSchedule.builder()
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(22, 0))
                .frequencyMinutes(15)
                .build();

        WeekSchedule weekends = WeekSchedule.builder()
                .startTime(LocalTime.of(7, 0))
                .endTime(LocalTime.of(20, 0))
                .frequencyMinutes(20)
                .build();

        Schedule schedule = Schedule.builder()
                .weekdays(weekdays)
                .weekends(weekends)
                .build();

        ObjectId routeId = new ObjectId("507f1f77bcf86cd799439011");
        route = Route.builder()
                .routeId(routeId)
                .routeName("Test Route")
                .stops(stops)
                .schedule(schedule)
                .build();

        createdEvent = RouteCreatedEvent.builder()
                .routeId(routeId)
                .routeName("Test Route")
                .stops(stops)
                .schedule(schedule)
                .build();

        deletedEvent = RouteDeletedEvent.builder()
                .routeId(routeId)
                .build();

        updatedEvent = RouteUpdatedEvent.builder()
                .routeId(routeId)
                .routeName("Updated Test Route")
                .stops(stops)
                .schedule(schedule)
                .build();
    }

    @Test
    @DisplayName("Test processRouteCreatedEvent - Success")
    void processRouteCreatedEvent_Success() {
        when(routeCommandRepository.save(route)).thenReturn(Mono.just(route));

        StepVerifier.create(routeEventProcessor.processRouteCreatedEvent(createdEvent))
                .verifyComplete();

        verify(routeCommandRepository, times(1)).save(route);
    }

    @Test
    @DisplayName("Test processRouteCreatedEvent - Error")
    void processRouteCreatedEvent_Error() {
        when(routeCommandRepository.save(route)).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(routeEventProcessor.processRouteCreatedEvent(createdEvent))
                .expectErrorMessage("Database error")
                .verify();

        verify(routeCommandRepository, times(1)).save(route);
    }

    @Test
    @DisplayName("Test processRouteDeletedEvent - Success")
    void processRouteDeletedEvent_Success() {
        when(routeCommandRepository.deleteById(deletedEvent.getRouteId())).thenReturn(Mono.empty());

        StepVerifier.create(routeEventProcessor.processRouteDeletedEvent(deletedEvent))
                .verifyComplete();

        verify(routeCommandRepository, times(1)).deleteById(deletedEvent.getRouteId());
    }

    @Test
    @DisplayName("Test processRouteDeletedEvent - Error")
    void processRouteDeletedEvent_Error() {
        when(routeCommandRepository.deleteById(deletedEvent.getRouteId()))
                .thenReturn(Mono.error(new RuntimeException("Deletion error")));

        StepVerifier.create(routeEventProcessor.processRouteDeletedEvent(deletedEvent))
                .expectErrorMessage("Deletion error")
                .verify();

        verify(routeCommandRepository, times(1)).deleteById(deletedEvent.getRouteId());
    }

    @Test
    @DisplayName("Test processRouteUpdatedEvent - Success")
    void processRouteUpdatedEvent_Success() {
        when(routeCommandRepository.findById(updatedEvent.getRouteId())).thenReturn(Mono.just(route));
        when(routeCommandRepository.save(route)).thenReturn(Mono.just(route));

        StepVerifier.create(routeEventProcessor.processRouteUpdatedEvent(updatedEvent))
                .verifyComplete();

        verify(routeCommandRepository, times(1)).findById(updatedEvent.getRouteId());
        verify(routeCommandRepository, times(1)).save(route);
    }

    @Test
    @DisplayName("Test processRouteUpdatedEvent - Route Not Found")
    void processRouteUpdatedEvent_RouteNotFound() {
        when(routeCommandRepository.findById(updatedEvent.getRouteId())).thenReturn(Mono.empty());

        StepVerifier.create(routeEventProcessor.processRouteUpdatedEvent(updatedEvent))
                .verifyComplete();

        verify(routeCommandRepository, times(1)).findById(updatedEvent.getRouteId());
        verify(routeCommandRepository, never()).save(any(Route.class));
    }

    @Test
    @DisplayName("Test processRouteUpdatedEvent - Error")
    void processRouteUpdatedEvent_Error() {
        when(routeCommandRepository.findById(updatedEvent.getRouteId())).thenReturn(Mono.just(route));
        when(routeCommandRepository.save(route)).thenReturn(Mono.error(new RuntimeException("Update error")));

        StepVerifier.create(routeEventProcessor.processRouteUpdatedEvent(updatedEvent))
                .expectErrorMessage("Update error")
                .verify();

        verify(routeCommandRepository, times(1)).findById(updatedEvent.getRouteId());
        verify(routeCommandRepository, times(1)).save(route);
    }
}
