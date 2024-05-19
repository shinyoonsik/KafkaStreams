package com.digitaltwin.serdes;

import com.digitaltwin.model.DigitalTwin;
import com.digitaltwin.serdes.deserialization.DigitalTwinDeserializer;
import com.digitaltwin.serdes.serialization.DigitalTwinSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

public class DigitalTwinSerdes {

    public static Serde<DigitalTwin> getDigitalTwin() {
        return Serdes.serdeFrom(new DigitalTwinSerializer(), new DigitalTwinDeserializer());
    }
}