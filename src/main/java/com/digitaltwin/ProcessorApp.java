package com.digitaltwin;

import com.digitaltwin.model.DigitalTwin;
import com.digitaltwin.model.TurbineState;
import com.digitaltwin.processors.DigitalTwinProcessor;
import com.digitaltwin.processors.HighWindsAlertProcessor;
import com.digitaltwin.serdes.DigitalTwinSerdes;
import com.digitaltwin.serdes.TurbineStateSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Properties;

/**
 * TODO 리스트
 * 1. logging 처리
 */
public class ProcessorApp {
    public static void main(String[] args) {

        Topology topology = getTopology();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "DigitalTwin-Application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);

        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);

        // JVM이 종료될 때 호출될 작업을 등록한다. 애플리케이션이 외부 신호 (예: SIGTERM, SIGINT)로 인해 종료되거나, 다른 예기치 않은 종료가 발생시 호출
        // 1. 사용자나 시스템에 의해 JVM이 종료될 때 (예: kill 명령어나 Ctrl+C)
        // 2. 시스템 종료나 재부팅
        // 3. JVM 종료 명령이 내려졌을 때
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Kafka Streams...");
            kafkaStreams.close();
        }));

        kafkaStreams.start();
    }

    public static Topology getTopology(){
        Topology topology = new Topology();

        Serde<DigitalTwin> digitalTwinSerdes = DigitalTwinSerdes.getDigitalTwin();
        Serde<TurbineState> turbineStateSerdes = TurbineStateSerdes.getTurbineStateSerde();

        topology.addSource("Desired State Events", Serdes.String().deserializer(), turbineStateSerdes.deserializer(), "desired-state-events");
        topology.addSource("Reported State Events", Serdes.String().deserializer(), turbineStateSerdes.deserializer(), "reported-state-events");

        // 상태가 없는 하지만 위험한 바람 상태가 감지되면 셧다운 신호를 생성하는 프로세서
        topology.addProcessor("High Winds Alert Processor", HighWindsAlertProcessor::new, "Reported State Events"); // 연결짓고싶은 부모 프로세서 이름);

        // digital twin레코드 생성(reported record + 직접 신호를 발생시킨 desired record)
        topology.addProcessor("Digital Twin Processor", DigitalTwinProcessor::new, "High Winds Alert Processor", "Desired State Events");

        // state store for digital twin records
        StoreBuilder<KeyValueStore<String, DigitalTwin>> storeBuilder = Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore("digital-twin-store"), Serdes.String(), digitalTwinSerdes);

        topology.addStateStore(storeBuilder,"Digital Twin Processor");
        topology.addSink("Digital Twin Sink", "digital-twins", Serdes.String().serializer(), digitalTwinSerdes.serializer(), "Digital Twin Processor");
        topology.addSink("Dangerous Wind Detected", "dangerous-wind-detected", Serdes.String().serializer(), turbineStateSerdes.serializer(), "High Winds Alert Processor");

        return topology;
    }
}
