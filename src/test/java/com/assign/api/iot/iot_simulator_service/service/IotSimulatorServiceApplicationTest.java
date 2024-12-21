package com.assign.api.iot.iot_simulator_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class IotSimulatorServiceApplicationTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @InjectMocks
    private IoTDataSimulatorService ioTDataSimulatorService;
    private ObjectMapper objectMapper;
    private SendResult<String, String> dummySendResult;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        String payload;
        try {
            payload = objectMapper.writeValueAsString(Map.of(
                    "sensorDeviceType", "THERMOSTAT",
                    "deviceId", UUID.randomUUID().toString(),
                    "timestamp", System.currentTimeMillis(),
                    "value", Math.random() * 100
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("iot-data-topic", 0), 0, 0, System.currentTimeMillis(), 0L, 0, 0);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("iot-data-topic", payload);
        dummySendResult = new SendResult<>(producerRecord, metadata);

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(dummySendResult);

        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(future);
    }

    @Test
    void testSendIoTData_Success() {

        ioTDataSimulatorService.sendIoTData();

        verify(kafkaTemplate, times(1)).send(eq("iot-data-topic"), anyString());
    }

    @Test
    void testSendIoTData_JsonProcessingException() {
        doThrow(new RuntimeException("Kafka failed to send message exception"))
                .when(kafkaTemplate).send(anyString(), anyString());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ioTDataSimulatorService.sendIoTData());
        assertTrue(exception.getMessage().contains("Kafka failed to send message exception"));

        verify(kafkaTemplate).send(eq("iot-data-topic"), anyString());
    }
}
