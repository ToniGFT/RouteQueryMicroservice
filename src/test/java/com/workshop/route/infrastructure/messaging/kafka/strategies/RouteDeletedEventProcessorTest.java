package com.workshop.route.infrastructure.messaging.kafka.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.application.services.kafka.RouteEventProcessor;
import com.workshop.route.domain.events.RouteDeletedEvent;
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

class RouteDeletedEventProcessorTest {

    @Mock
    private RouteEventProcessor routeEventProcessor;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Logger logger;

    @InjectMocks
    private RouteDeletedEventProcessor eventProcessor;

    private final String validMessage = "{\"routeId\":\"507f1f77bcf86cd799439011\",\"routeName\":\"Test Route\"}";
    private final String invalidMessage = "invalid json";

    private RouteDeletedEvent routeDeletedEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routeDeletedEvent = new RouteDeletedEvent();
        routeDeletedEvent.setRouteId(new ObjectId("507f1f77bcf86cd799439011"));
    }

    @Test
    void testProcess_Successful() throws Exception {
        when(objectMapper.readValue(validMessage, RouteDeletedEvent.class)).thenReturn(routeDeletedEvent);
        when(routeEventProcessor.processRouteDeletedEvent(routeDeletedEvent)).thenReturn(Mono.empty());

        Mono<Void> result = eventProcessor.process(validMessage);

        StepVerifier.create(result)
                .verifyComplete();

        verify(objectMapper, times(1)).readValue(validMessage, RouteDeletedEvent.class);
        verify(routeEventProcessor, times(1)).processRouteDeletedEvent(routeDeletedEvent);
    }

    @Test
    void testProcess_DeserializationError() throws Exception {
        when(objectMapper.readValue(invalidMessage, RouteDeletedEvent.class)).thenThrow(new RuntimeException("Deserialization error"));

        Mono<Void> result = eventProcessor.process(invalidMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().contains("Failed to process RouteDeletedEvent"))
                .verify();

        verify(objectMapper, times(1)).readValue(invalidMessage, RouteDeletedEvent.class);
        verifyNoInteractions(routeEventProcessor);
    }

    @Test
    void testProcess_ProcessingError() throws Exception {
        when(objectMapper.readValue(validMessage, RouteDeletedEvent.class)).thenReturn(routeDeletedEvent);
        when(routeEventProcessor.processRouteDeletedEvent(routeDeletedEvent))
                .thenReturn(Mono.error(new RuntimeException("Processing error")));

        Mono<Void> result = eventProcessor.process(validMessage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Processing error"))
                .verify();

        verify(objectMapper, times(1)).readValue(validMessage, RouteDeletedEvent.class);
        verify(routeEventProcessor, times(1)).processRouteDeletedEvent(routeDeletedEvent);
    }
}
