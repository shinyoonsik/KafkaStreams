package com.tweet.serdes.deserialization;

import com.tweet.model.Tweet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

@Slf4j
public class TweetDeserializer implements Deserializer<Tweet> {

    private final ObjectMapper objectMapper;

    public TweetDeserializer() {
        this.objectMapper = new ObjectMapper();

        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @Override
    public Tweet deserialize(String topic, byte[] data) {
        log.info("Tweet 역직렬화");

        try {
            if(data == null) return null;

            return this.objectMapper.readValue(new String(data, StandardCharsets.UTF_8), Tweet.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize the Json message to the Tweet", e);
        }
    }
}
