package com.digitaltwin.serdes.serialization;

import com.digitaltwin.model.TurbineState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class TurbineStateSerializer implements Serializer<TurbineState> {
    private final ObjectMapper objectMapper;

    public TurbineStateSerializer(){
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
    @Override
    public byte[] serialize(String topic, TurbineState turbineState) {
        try {
            if(turbineState == null) return null;

            return objectMapper.writeValueAsString(turbineState).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize the TurbineState", e);
        }
    }
}
