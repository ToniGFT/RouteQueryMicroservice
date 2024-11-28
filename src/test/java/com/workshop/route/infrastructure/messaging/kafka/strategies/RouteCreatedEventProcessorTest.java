package com.workshop.route.infrastructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.application.services.kafka.RouteEventProcessor;
import com.workshop.route.domain.events.RouteCreatedEvent;
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

class RouteCreatedEventProcessorTest {

    @Mock
    private RouteEventProcessor routeEventProcessor;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Logger logger;

    @InjectMocks
    private RouteCreatedEventProcessor eventProcessor;

    private final String validMessage = "{\"id\":\"route123\",\"name\":\"Test Route\"}";
    private final String invalidMessage = "invalid json";

    private RouteCreatedEvent routeCreatedEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routeCreatedEvent = new RouteCreatedEvent();
        routeCreatedEvent.setRouteId(new ObjectId("507f1f77bcf86cd799439011"));
        routeCreatedEvent.setRouteName("Test Route");
    }

    @Test
    void testProcess_Successful() throws Exception {
        when(objectMapper.readValue(validMessage, RouteCreatedEvent.class)).thenReturn(routeCreatedEvent);
        when(routeEventProcessor.processRouteCreatedEvent(routeCreatedEvent)).thenReturn(Mono.empty());

        Mono<Void> result = eventProcessor.process(validMessage);

        StepVerifier.create(result)
                .verifyComplete();

        verify(objectMapper, times(1)).readValue(validMessage, RouteCreatedEvent.class);
        verify(routeEventProcessor, times(1)).processRouteCreatedEvent(routeCreatedEvent);
    }

    @Test
    void testProcess_DeserializationError() throws Exception {
        when(objectMapper.readValue(invalidMessage, RouteCreatedEvent.class)).thenThrow(new RuntimeException("Deserialization error"));

        Mono<Void> result = eventProcessor.process(invalidMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().contains("Failed to process RouteCreatedEvent"))
                .verify();

        verify(objectMapper, times(1)).readValue(invalidMessage, RouteCreatedEvent.class);
        verifyNoInteractions(routeEventProcessor);
    }

    @Test
    void testProcess_ProcessingError() throws Exception {
        when(objectMapper.readValue(validMessage, RouteCreatedEvent.class)).thenReturn(routeCreatedEvent);
        when(routeEventProcessor.processRouteCreatedEvent(routeCreatedEvent))
                .thenReturn(Mono.error(new RuntimeException("Processing error")));

        Mono<Void> result = eventProcessor.process(validMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Processing error"))
                .verify();

        verify(objectMapper, times(1)).readValue(validMessage, RouteCreatedEvent.class);
        verify(routeEventProcessor, times(1)).processRouteCreatedEvent(routeCreatedEvent);
    }
}
