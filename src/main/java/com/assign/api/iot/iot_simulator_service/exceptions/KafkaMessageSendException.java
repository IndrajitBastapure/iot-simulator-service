package com.assign.api.iot.iot_simulator_service.exceptions;

public class KafkaMessageSendException extends RuntimeException {
    public KafkaMessageSendException(String message) {
        super(message);
    }
}