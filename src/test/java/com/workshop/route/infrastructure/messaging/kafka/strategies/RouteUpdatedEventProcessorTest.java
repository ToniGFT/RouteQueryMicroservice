package com.workshop.route.infrastructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.application.services.kafka.RouteEventProcessor;
import com.workshop.route.domain.events.RouteUpdatedEvent;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class RouteUpdatedEventProcessorTest {

    @Mock
    private RouteEventProcessor routeEventProcessor;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Logger logger;

    @InjectMocks
    private RouteUpdatedEventProcessor eventProcessor;

    private final String validMessage = "{\"routeId\":\"507f1f77bcf86cd799439011\",\"routeName\":\"Updated Route\"}";
    private final String invalidMessage = "invalid json";

    private RouteUpdatedEvent routeUpdatedEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routeUpdatedEvent = new RouteUpdatedEvent();
        routeUpdatedEvent.setRouteId(new ObjectId("507f1f77bcf86cd799439011"));
        routeUpdatedEvent.setRouteName("Updated Route");
    }

    @Test
    void testProcess_Successful() throws Exception {
        when(objectMapper.readValue(validMessage, RouteUpdatedEvent.class)).thenReturn(routeUpdatedEvent);
        when(routeEventProcessor.processRouteUpdatedEvent(routeUpdatedEvent)).thenReturn(Mono.empty());

        Mono<Void> result = eventProcessor.process(validMessage);

        StepVerifier.create(result)
                .verifyComplete();

        verify(objectMapper, times(1)).readValue(validMessage, RouteUpdatedEvent.class);
        verify(routeEventProcessor, times(1)).processRouteUpdatedEvent(routeUpdatedEvent);
    }

    @Test
    void testProcess_DeserializationError() throws Exception {
        when(objectMapper.readValue(invalidMessage, RouteUpdatedEvent.class)).thenThrow(new RuntimeException("Deserialization error"));

        Mono<Void> result = eventProcessor.process(invalidMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().contains("Failed to process RouteUpdatedEvent"))
                .verify();

        verify(objectMapper, times(1)).readValue(invalidMessage, RouteUpdatedEvent.class);
        verifyNoInteractions(routeEventProcessor);
    }

    @Test
    void testProcess_ProcessingError() throws Exception {
        when(objectMapper.readValue(validMessage, RouteUpdatedEvent.class)).thenReturn(routeUpdatedEvent);
        when(routeEventProcessor.processRouteUpdatedEvent(routeUpdatedEvent))
                .thenReturn(Mono.error(new RuntimeException("Processing error")));

        Mono<Void> result = eventProcessor.process(validMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Processing error"))
                .verify();

        verify(objectMapper, times(1)).readValue(validMessage, RouteUpdatedEvent.class);
        verify(routeEventProcessor, times(1)).processRouteUpdatedEvent(routeUpdatedEvent);
    }
}
