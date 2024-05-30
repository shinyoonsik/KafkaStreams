package com.game;

import com.game.service.GameBoardService;
import com.game.topology.GameBoardTopology;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.HostInfo;

import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class Main {

    // TODO README작성 하고 topic과 컴포즈 파일 스크립트 자동실항하게 바꿔
    public static void main(String[] args) {
        // 프로젝트 루트 디렉토리 경로
        String projectRootDir = Paths.get("").toAbsolutePath().toString();

        // state-log 디렉토리 경로를 설정
        String stateDir = Paths.get(projectRootDir, "state-log").toString();

        Topology topology = GameBoardTopology.build();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Game-Board");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        props.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0); // 캐시 비활성화도 포함됨; throughput 감소, latency 증가
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        props.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);

        log.info("Game-Board 시작!");

        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);

        /**
         * SIGTERM(kill -15): 정상 종료 요청시 호루
         * SIGINT(ctrl+c): 터미널에서 실행중인 프로세스 중단 요청
         */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Kafka Streams...");
            kafkaStreams.close();
        }));

        kafkaStreams.start();

        // start the REST service for monitoring
        String host = "localhost";
        Integer port = 7000;
        HostInfo hostInfo = new HostInfo(host, port);
        GameBoardService gameBoardService = new GameBoardService(hostInfo, kafkaStreams);
        gameBoardService.start();
    }
}
