package com.digitaltwin.serdes;

import com.digitaltwin.model.TurbineState;
import com.digitaltwin.serdes.deserialization.TurbineStateDeserializer;
import com.digitaltwin.serdes.serialization.TurbineStateSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class TurbineStateSerdes {
    public static Serde<TurbineState> getTurbineStateSerde() {
        return Serdes.serdeFrom(new TurbineStateSerializer(), new TurbineStateDeserializer());
    }
}