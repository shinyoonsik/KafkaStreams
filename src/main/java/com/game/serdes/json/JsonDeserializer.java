package com.game.serdes.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

@Slf4j
public class JsonDeserializer<T> implements Deserializer<T> {
    private final Class<T> targetType;
    private final ObjectMapper objectMapper;

    public JsonDeserializer(Class<T> targetType) {
        this.targetType = targetType;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        this.objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try{
            if(data == null) {
                log.info("null인 data는 역직렬화를 수행할수 없다");
                return null;
            }

            return this.objectMapper.readValue(new String(data, StandardCharsets.UTF_8), targetType);
        }catch (Exception ex){
            throw new RuntimeException("Failed to deserialize the Json message to the " + targetType, ex);
        }
    }
}
