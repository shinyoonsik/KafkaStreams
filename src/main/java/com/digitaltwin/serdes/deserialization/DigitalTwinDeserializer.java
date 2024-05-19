package com.digitaltwin.serdes;

import com.digitaltwin.model.DigitalTwin;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class DigitalTwinDeserializer implements Deserializer<DigitalTwin> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DigitalTwin deserialize(String topic, byte[] data) {
        try {
            if (data == null) return null;

            return objectMapper.readValue(data, DigitalTwin.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize the Json message to the DigitalTwin", e);
        }
    }
}
