package com.digitaltwin.serdes.deserialization;

import com.digitaltwin.model.DigitalTwin;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

public class DigitalTwinDeserializer implements Deserializer<DigitalTwin> {
    private final ObjectMapper objectMapper;

    public DigitalTwinDeserializer() {
        this.objectMapper = new ObjectMapper();

        // JSON에 POJO 클래스에 없는 속성이 포함되어 있어도 예외가 발생하지 않고 무시
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 빈 문자열이 있는 JSON 필드는 null 값으로 역직렬된다
        this.objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @Override
    public DigitalTwin deserialize(String topic, byte[] data) {
        try {
            if (data == null) return null;

            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), DigitalTwin.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize the Json message to the DigitalTwin", e);
        }
    }
}
