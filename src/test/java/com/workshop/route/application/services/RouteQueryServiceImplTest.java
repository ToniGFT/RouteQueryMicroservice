package com.workshop.route.application.services;

import com.workshop.route.domain.model.aggregates.Route;
import com.workshop.route.domain.model.entities.Schedule;
import com.workshop.route.domain.model.entities.Stop;
import com.workshop.route.domain.model.valueobjects.Coordinates;
import com.workshop.route.domain.model.valueobjects.WeekSchedule;
import com.workshop.route.domain.repository.RouteQueryRepository;
import jakarta.validation.Validator;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;

@DisplayName("RouteServiceImpl Unit Tests")
class RouteQueryServiceImplTest {

    @Mock
    private RouteQueryRepository routeQueryRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private RouteQueryServiceImpl routeService;

    private Route route;

    private Route invalidRoute;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Stop stop1 = Stop.builder()
                .stopId("1")
                .stopName("Estación Central")
                .coordinates(new Coordinates(40.712776, -74.005974))
                .arrivalTimes(List.of("08:00", "08:30", "09:00"))
                .build();

        Stop stop2 = Stop.builder()
                .stopId("2")
                .stopName("Plaza Norte")
                .coordinates(new Coordinates(40.730610, -73.935242))
                .arrivalTimes(List.of("08:15", "08:45", "09:15"))
                .build();

        Stop stop3 = Stop.builder()
                .stopId("3")
                .stopName("Terminal Norte")
                .coordinates(new Coordinates(40.748817, -73.985428))
                .arrivalTimes(List.of("08:30", "09:00", "09:30"))
                .build();

        List<Stop> stops = List.of(stop1, stop2, stop3);

        WeekSchedule weekdaysSchedule = new WeekSchedule(
                LocalTime.of(6, 0),
                LocalTime.of(22, 0),
                15
        );

        WeekSchedule weekendsSchedule = new WeekSchedule(
                LocalTime.of(7, 0),
                LocalTime.of(20, 0),
                20
        );

        Schedule schedule = new Schedule(weekdaysSchedule, weekendsSchedule);

        ObjectId id = new ObjectId("507f1f77bcf86cd799439011");
        route = Route.builder()
                .routeId(id)
                .routeName("Ruta Centro-Norte")
                .stops(stops)
                .schedule(schedule)
                .build();

        invalidRoute = Route.builder().build();
    }

    @Test
    @DisplayName("Test getRouteById - Route Found")
    void getRouteById_Found() {
        ObjectId id = new ObjectId("507f1f77bcf86cd799439011");
        when(routeQueryRepository.findById(id)).thenReturn(Mono.just(route));

        StepVerifier.create(routeService.getRouteById(id))
                .expectNext(route)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test getRouteById - Route Not Found")
    void getRouteById_NotFound() {
        ObjectId id = new ObjectId("507f1f77bcf86cd799439012"); // Usar un ObjectId válido pero diferente
        when(routeQueryRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(routeService.getRouteById(id))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test getAllRoutes - Return All Routes")
    void getAllRoutes() {
        when(routeQueryRepository.findAll()).thenReturn(Flux.just(route));

        StepVerifier.create(routeService.getAllRoutes())
                .expectNext(route)
                .verifyComplete();
    }
}
