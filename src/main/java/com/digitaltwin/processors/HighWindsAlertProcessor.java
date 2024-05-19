package com.digitaltwin.processors;

import com.digitaltwin.model.Enum.Power;
import com.digitaltwin.model.Enum.Type;
import com.digitaltwin.model.TurbineState;
import lombok.NoArgsConstructor;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

@NoArgsConstructor
public class HighWindsAlertProcessor implements Processor<String, TurbineState, String, TurbineState> {
    private ProcessorContext<String, TurbineState> context;

    @Override
    public void init(ProcessorContext<String, TurbineState> context) {
        this.context = context;
    }

    // 이 프로세서가 새로운 record를 받을 때마다 호출된다. 비즈니스 로직이 들어가는 메소드
    @Override
    public void process(Record<String, TurbineState> record) {
        TurbineState turbineStateValue = record.value();

        if (turbineStateValue.getWindSpeedMph() > 65 && turbineStateValue.getPower() == Power.ON) {
            System.out.println("high winds detected. sending shutdown signal");
            System.out.println("HighWindsAlertProcessor에 입력된 record: " + turbineStateValue);

            TurbineState desiredTurbineState = TurbineState.clone(turbineStateValue);
            desiredTurbineState.setPower(Power.OFF);
            desiredTurbineState.setType(Type.DESIRED);

            Record<String, TurbineState> newRecord = new Record<>(record.key(), desiredTurbineState, record.timestamp());

            // 특정 싱크로 전달
            this.context.forward(newRecord, "Dangerous Wind Detected");
        } else {
            // 기본 흐름을 따르도록 설정
            this.context.forward(record, "Digital Twin Processor");
        }
    }

    // 프로세서가 종료될 때마다 호출된다
    // 프로세서나 로컬 자원의 정리 로직을 포함한다
    // 단, 상태 저장소와 같은 KafkaStreams 관리 자원들은 정리하지 말아야 한다.
    @Override
    public void close() {
    }
}
