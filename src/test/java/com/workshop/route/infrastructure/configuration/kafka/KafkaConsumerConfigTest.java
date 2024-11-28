package com.workshop.route.infrastructure.configuration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("KafkaConsumerConfig Integration Tests")
class KafkaConsumerConfigTest {

    @Autowired
    private ReceiverOptions<String, String> receiverOptions;

    @Test
    @DisplayName("Test ReceiverOptions Bean Creation")
    void testReceiverOptionsBeanCreation() {
        assertThat(receiverOptions).isNotNull();

        Map<String, Object> consumerProps = receiverOptions.consumerProperties();

        assertThat(consumerProps.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)).isEqualTo("127.0.0.1:9092");
        assertThat(consumerProps.get(ConsumerConfig.GROUP_ID_CONFIG)).isEqualTo("route-query-group");
        assertThat(consumerProps.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)).isEqualTo(StringDeserializer.class);
        assertThat(consumerProps.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)).isEqualTo(StringDeserializer.class);
        assertThat(consumerProps.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)).isEqualTo("earliest");
        assertThat(consumerProps.get(ConsumerConfig.FETCH_MIN_BYTES_CONFIG)).isEqualTo(1);
        assertThat(consumerProps.get(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG)).isEqualTo(500);
        assertThat(consumerProps.get(ConsumerConfig.MAX_POLL_RECORDS_CONFIG)).isEqualTo(10);

        Set<String> subscribedTopics = new HashSet<>(Objects.requireNonNull(receiverOptions.subscriptionTopics()));
        assertThat(subscribedTopics).contains("route-events");
    }
}
