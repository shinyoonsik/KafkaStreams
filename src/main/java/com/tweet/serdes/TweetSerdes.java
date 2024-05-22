package com.crypto.serdes;

import com.crypto.model.Tweet;
import com.crypto.serdes.deserialization.TweetDeserializer;
import com.crypto.serdes.serialization.TweetSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class TweetSerdes implements Serde<Tweet> {

    @Override
    public Serializer<Tweet> serializer() {
        return new TweetSerializer();
    }

    @Override
    public Deserializer<Tweet> deserializer() {
        return new TweetDeserializer();
    }
}
