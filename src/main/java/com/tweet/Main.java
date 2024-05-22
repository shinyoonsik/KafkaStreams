package com.tweet;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

@Slf4j
public class Main {
    public static void main(String[] args) {
        Topology topology = CensorTweetsTopology.build();

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "Censor-Tweets-Application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);

        KafkaStreams kafkaStreams = new KafkaStreams(topology, config);

        // 프로세스 중단 시그널 -> 카프카 스트림즈 graceful shutdown(서비스를 안전하고 질서있게 종류하는 것을 말함!)
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

        log.info("Start Twitter Streams");
        kafkaStreams.start();
    }
}
