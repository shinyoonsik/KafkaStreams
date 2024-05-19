package com.digitaltwin.serdes;

import com.digitaltwin.model.TurbineState;
import com.digitaltwin.serdes.deserialization.TurbineStateDeserializer;
import com.digitaltwin.serdes.serialization.TurbineStateSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class TurbineStateSerdes implements Serde<TurbineState> {
    private final Serializer<TurbineState> serializer;
    private final Deserializer<TurbineState> deserializer;

    public TurbineStateSerdes() {
        this.serializer = new TurbineStateSerializer();
        this.deserializer = new TurbineStateDeserializer();
    }

    @Override
    public Serializer<TurbineState> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<TurbineState> deserializer() {
        return deserializer;
    }
}