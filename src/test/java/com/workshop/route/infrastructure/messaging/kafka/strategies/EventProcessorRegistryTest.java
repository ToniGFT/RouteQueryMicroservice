package com.workshop.route.infrastructure.messaging.kafka.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EventProcessorRegistryTest {

    private EventProcessorRegistry eventProcessorRegistry;
    private EventProcessor<String> testProcessorA;
    private EventProcessor<String> testProcessorB;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testProcessorA = spy(new TestEventProcessorA());
        testProcessorB = spy(new TestEventProcessorB());

        List<EventProcessor<?>> processors = Arrays.asList(
                testProcessorA,
                testProcessorB
        );

        eventProcessorRegistry = new EventProcessorRegistry(processors);
    }

    @Test
    void testProcess_KnownType_TypeA() {
        String message = "Test message for TYPE_A";
        Mono<Void> result = eventProcessorRegistry.process("TYPE_A", message);

        StepVerifier.create(result)
                .verifyComplete();

        verify(testProcessorA, times(1)).process(message);
        verify(testProcessorB, never()).process(anyString());
    }

    @Test
    void testProcess_KnownType_TypeB() {
        String message = "Test message for TYPE_B";
        Mono<Void> result = eventProcessorRegistry.process("TYPE_B", message);

        StepVerifier.create(result)
                .verifyComplete();

        verify(testProcessorB, times(1)).process(message);
        verify(testProcessorA, never()).process(anyString());
    }

    @Test
    void testProcess_UnknownType() {
        String message = "Test message for UNKNOWN_TYPE";
        Mono<Void> result = eventProcessorRegistry.process("UNKNOWN_TYPE", message);

        StepVerifier.create(result)
                .verifyComplete();

        verify(testProcessorA, never()).process(anyString());
        verify(testProcessorB, never()).process(anyString());

    }

    @Component("TYPE_A")
    static class TestEventProcessorA implements EventProcessor<String> {
        @Override
        public Mono<Void> process(String message) {
            return Mono.empty();
        }
    }

    @Component("TYPE_B")
    static class TestEventProcessorB implements EventProcessor<String> {
        @Override
        public Mono<Void> process(String message) {
            return Mono.empty();
        }
    }
}
