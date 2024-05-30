package com.game.serdes.json;

import com.game.model.HighScores;
import com.game.model.Player;
import com.game.model.Product;
import com.game.model.ScoreEvent;
import com.game.model.join.projection.ResultRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class JsonSerdes {

    public static Serde<ScoreEvent> getScoreEventSerde(){
        JsonSerializer<ScoreEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<ScoreEvent> deserializer = new JsonDeserializer<>(ScoreEvent.class);

        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<Player> getPlayerSerde(){
        JsonSerializer<Player> serializer = new JsonSerializer<>();
        JsonDeserializer<Player> deserializer = new JsonDeserializer<>(Player.class);

        return  Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<Product> getProductSerde(){
        JsonSerializer<Product> serializer = new JsonSerializer<>();
        JsonDeserializer<Product> deserializer = new JsonDeserializer<>(Product.class);

        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<ResultRecord> getResultRecordSerde(){
        JsonSerializer<ResultRecord> serializer = new JsonSerializer<>();
        JsonDeserializer<ResultRecord> deserializer = new JsonDeserializer<>(ResultRecord.class);

        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<HighScores> getHighScoresSerde(){
        JsonSerializer<HighScores> serializer = new JsonSerializer<>();
        JsonDeserializer<HighScores> deserializer = new JsonDeserializer<>(HighScores.class);

        return Serdes.serdeFrom(serializer, deserializer);
    }

}
