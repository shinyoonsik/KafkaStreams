package com.digitaltwin.serdes;

import com.digitaltwin.model.TurbineState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class TurbineStateDeserializer implements Deserializer<TurbineState> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public TurbineState deserialize(String topic, byte[] data) {
        try {
            if (data == null) return null;

            return objectMapper.readValue(data, TurbineState.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize the Json message to the TurbineState", e);
        }
    }
}
