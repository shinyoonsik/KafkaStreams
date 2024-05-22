package com.crypto.serdes.serialization;

import com.crypto.model.Tweet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

@Slf4j
public class TweetSerializer implements Serializer<Tweet> {

    private final ObjectMapper objectMapper;

    public TweetSerializer() {
        this.objectMapper = new ObjectMapper();

        // 직렬화할 속성이 없는 객체에 대해, 예외를 던질 것인가!(true) or 빈 객체로 직렬화할 것인가!(false)
        // 직렬화할 속성이 없다 == 멤버 필드가 없는 경우 or 접근자가 없는 경우, 주석(@JsonProperty, @JsonSerialize)이 없는 경우
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public byte[] serialize(String topic, Tweet tweet) {
        log.info("Tweet 직렬화");

        try {
            if(tweet == null) return null;

            return this.objectMapper.writeValueAsString(tweet).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize the Tweet", e);
        }
    }
}
