package com.digitaltwin.processors;

import com.digitaltwin.model.DigitalTwin;
import com.digitaltwin.model.Enum.Type;
import com.digitaltwin.model.TurbineState;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;
import java.time.Instant;

public class DigitalTwinProcessor implements Processor<String, TurbineState, String, DigitalTwin> {

    /**
     * Processor API의 스케줄링 기능을 담당하는 punctuator
     *
     * process함수와 스케줄링되 있는 punctuator의 enforceTtl함수는 동일 스레드에서 실행된다.
     * 즉, punctuator만을 위한 백그라운드 스레드가 없다)
     * 따라서, 동시성 문제에 대해서 걱정할 필요가 없다
     */
    private Cancellable punctuator;
    private KeyValueStore<String, DigitalTwin> kvStore;
    private ProcessorContext<String, DigitalTwin> context;

    @Override
    public void init(ProcessorContext<String, DigitalTwin> context) {
        this.context = context;
        this.kvStore = context.getStateStore("digital-twin-store");

        // WALL_CLOCK_TIME기준으로 매 5분마다 주기적으로 enforceTtl()를 실행
        this.punctuator = this.context.schedule(
                Duration.ofMinutes(1),
                PunctuationType.WALL_CLOCK_TIME,
                this::enforceTtl
        );
    }

    @Override
    public void process(Record<String, TurbineState> record) {
        String key = record.key();
        TurbineState value = record.value();
        DigitalTwin digitalTwin = this.kvStore.get(key);
        System.out.println("DigitalTwinProcessor에 입력된 레코드: " + key + " " + value.getPower() + " " + value.getTimestamp() + " " + value.getWindSpeedMph());

        if(digitalTwin == null) {
            digitalTwin = new DigitalTwin();
        }

        if(value.getType() == Type.DESIRED){
            digitalTwin.setDesired(value);
        } else if(value.getType() == Type.REPORTED){
            digitalTwin.setReported(value);
        }

        this.kvStore.put(key, digitalTwin);

        Record<String, DigitalTwin> newRecord = new Record<>(record.key(), digitalTwin, record.timestamp());
        this.context.forward(newRecord);
     }

    // 호출 시점?? 카프카 스트림즈 애플리케이션을 완전히 셧다운 할 때!?
    @Override
    public void close() {
        this.punctuator.cancel();
    }

    public void enforceTtl(Long timestamp){
        try (KeyValueIterator<String, DigitalTwin> iter = kvStore.all()) {
            while (iter.hasNext()) {
                KeyValue<String, DigitalTwin> entry = iter.next();

                System.out.println("Checking to see if digital twin record has expired: " + entry.key);

                TurbineState lastReportedState = entry.value.getReported();
                if (lastReportedState == null) {
                    continue;
                }

                Instant lastUpdated = Instant.parse(lastReportedState.getTimestamp());
                long daysSinceLastUpdate = Duration.between(lastUpdated, Instant.now()).toDays();
                System.out.println("daysSinceLastUpdate: " + daysSinceLastUpdate);
                if (daysSinceLastUpdate >= 7) {
                    kvStore.delete(entry.key);
                }

                System.out.println("[Current rocksDB state]");
                kvStore.all().forEachRemaining(element ->
                        System.out.println("Key: " + element.key + ", Value: " + element.value)
                );
                System.out.println();
            }
        }
    }
}
