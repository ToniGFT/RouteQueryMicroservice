package com.workshop.route.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.route.infrastructure.messaging.kafka.strategies.EventProcessorRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

import static org.mockito.Mockito.*;

class RouteEventConsumerTest {

    @Mock
    private ReceiverOptions<String, String> receiverOptions;

    @Mock
    private EventProcessorRegistry eventProcessorRegistry;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaReceiver<String, String> kafkaReceiver;

    @InjectMocks
    private RouteEventConsumer routeEventConsumer;

    private MockedStatic<KafkaReceiver> kafkaReceiverStaticMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        kafkaReceiverStaticMock = mockStatic(KafkaReceiver.class);
        kafkaReceiverStaticMock.when(() -> KafkaReceiver.create(receiverOptions)).thenReturn(kafkaReceiver);
    }

    @AfterEach
    void tearDown() {
        kafkaReceiverStaticMock.close();
    }

    @Test
    void testConsumeEvents_SuccessfulProcessing() throws Exception {
        ReceiverRecord<String, String> record = mock(ReceiverRecord.class);
        ReceiverOffset offset = mock(ReceiverOffset.class);

        String message = "{\"type\":\"ROUTE_CREATED\",\"data\":{\"id\":\"1\"}}";
        String eventType = "ROUTE_CREATED";

        when(record.value()).thenReturn(message);
        when(record.receiverOffset()).thenReturn(offset);

        JsonNode jsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(message)).thenReturn(jsonNode);
        when(jsonNode.has("type")).thenReturn(true);
        when(jsonNode.get("type")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn(eventType);

        when(eventProcessorRegistry.process(eventType, message)).thenReturn(Mono.empty());

        when(kafkaReceiver.receive()).thenReturn(Flux.just(record));

        routeEventConsumer.consumeEvents();

        Thread.sleep(100);

        verify(eventProcessorRegistry, times(1)).process(eventType, message);

        verify(offset, times(1)).acknowledge();
    }

    @Test
    void testConsumeEvents_MessageWithoutType() throws Exception {
        ReceiverRecord<String, String> record = mock(ReceiverRecord.class);
        ReceiverOffset offset = mock(ReceiverOffset.class);

        String message = "{\"data\":{\"id\":\"1\"}}";

        when(record.value()).thenReturn(message);
        when(record.receiverOffset()).thenReturn(offset);

        JsonNode jsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(message)).thenReturn(jsonNode);
        when(jsonNode.has("type")).thenReturn(false);

        when(kafkaReceiver.receive()).thenReturn(Flux.just(record));

        routeEventConsumer.consumeEvents();

        Thread.sleep(100);

        verify(eventProcessorRegistry, never()).process(anyString(), anyString());

        verify(offset, times(1)).acknowledge();
    }

    @Test
    void testConsumeEvents_EventProcessorThrowsException() throws Exception {
        VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.create();

        try {
            ReceiverRecord<String, String> record = mock(ReceiverRecord.class);
            ReceiverOffset offset = mock(ReceiverOffset.class);

            String message = "{\"type\":\"ROUTE_CREATED\",\"data\":{\"routeId\":\"507f1f77bcf86cd799439011\",\"routeName\":\"Test Route\",\"stops\":[],\"schedule\":null}}";
            String eventType = "ROUTE_CREATED";

            when(record.value()).thenReturn(message);
            when(record.receiverOffset()).thenReturn(offset);

            JsonNode jsonNode = mock(JsonNode.class);
            JsonNode typeNode = mock(JsonNode.class);
            when(objectMapper.readTree(message)).thenReturn(jsonNode);
            when(jsonNode.has("type")).thenReturn(true);
            when(jsonNode.get("type")).thenReturn(typeNode);
            when(typeNode.asText()).thenReturn(eventType);

            when(eventProcessorRegistry.process(eventType, message)).thenReturn(Mono.error(new RuntimeException("Processing error")));

            when(kafkaReceiver.receive()).thenReturn(Flux.just(record));

            routeEventConsumer.consumeEvents();

            virtualTimeScheduler.advanceTimeBy(Duration.ofSeconds(15));

            verify(offset, never()).acknowledge();
        } finally {
            Schedulers.resetFactory();
        }
    }
}
