package com.digitaltwin.serdes;

import com.digitaltwin.model.DigitalTwin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class DigitalTwinSerializer implements Serializer<DigitalTwin> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(String topic, DigitalTwin digitalTwin) {
        try {
            if(digitalTwin == null) return null;

            return objectMapper.writeValueAsString(digitalTwin).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize the DigitalTwin", e);
        }
    }
}
