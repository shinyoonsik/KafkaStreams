package com.digitaltwin.serdes;

import com.digitaltwin.model.TurbineState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class TurbineStateSerializer implements Serializer<TurbineState> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(String topic, TurbineState turbineState) {
        try {
            if(turbineState == null) return null;

            return objectMapper.writeValueAsString(turbineState).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize the TurbineState", e);
        }
    }
}
