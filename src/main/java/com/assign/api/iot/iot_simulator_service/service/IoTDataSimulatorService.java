package com.assign.api.iot.iot_simulator_service.service;

import com.assign.api.iot.iot_simulator_service.exceptions.KafkaMessageSendException;
import com.assign.api.iot.iot_simulator_service.model.SensorDeviceType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class IoTDataSimulatorService {
    private static final Logger logger = LoggerFactory.getLogger(IoTDataSimulatorService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public IoTDataSimulatorService(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }
    @Scheduled(fixedRate = 1000)
    public void sendIoTData() {
        String payload = null;
        SensorDeviceType sensorDeviceType = SensorDeviceType.values()[ThreadLocalRandom.current().nextInt(SensorDeviceType.values().length)];
        try {
            payload = new ObjectMapper().writeValueAsString(Map.of(
                    "sensorDeviceType",sensorDeviceType.name(),
                    "deviceId", UUID.randomUUID().toString(),
                    "timestamp", System.currentTimeMillis(),
                    "value", Math.random() * 100
            ));
            logger.debug("Payload created: {}", payload);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize IoT data payload", e);
            throw new KafkaMessageSendException("Serialization failed for IoT data payload: " + e.getMessage());
        }
        try{
            kafkaTemplate.send("iot-data-topic", payload);
            logger.info("Message sent to Kafka: {}", payload);
        } catch (Exception e) {
            logger.error("Failed to send message to Kafka", e);
            throw new KafkaMessageSendException("Kafka send failed: " + e.getMessage());
        }
    }
}
