package com.game.serdes;

import com.game.model.Player;
import com.game.model.Product;
import com.game.model.ScoreEvent;
import com.game.model.join.ScoreWithPlayer;
import com.game.serdes.json.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

public class GameBoardTopology {
    public static void main(String[] args) {

    }

    public static Topology build(){
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // source processor
        KStream<String, ScoreEvent> scoreEventKStream = streamsBuilder
                .stream("score-events", Consumed.with(Serdes.ByteArray(), JsonSerdes.getScoreEventSerde()))
                .selectKey((k, v) -> v.getPlayerId().toString()); // 특정 조인을 수행할 때, 코-파티셔닝 요구사항을 만족시키기위해 필요

        // source processor
        KTable<String, Player> playersKTable = streamsBuilder
                .table("players", Consumed.with(Serdes.String(), JsonSerdes.getPlayerSerde()));

        // source processor
        GlobalKTable<String, Product> productGlobalKTable = streamsBuilder
                .globalTable("products", Consumed.with(Serdes.String(), JsonSerdes.getProductSerde()));

        // join scoreEvents & players
        Joined<String, ScoreEvent, Player> joinSerdes = Joined.with(Serdes.String(), JsonSerdes.getScoreEventSerde(), JsonSerdes.getPlayerSerde());

        // Projection: ValueJoiner는 단순히 조인에 참여하는 각 레코드를 새롭게 결합된 레코드로 만드는 객체
        ValueJoiner<ScoreEvent, Player, ScoreWithPlayer> scorePlayerJoiner = ScoreWithPlayer::new;

        // 첫번째 join: ScoreEvent & Player
        KStream<String, ScoreWithPlayer> scoreWithPlayer = scoreEventKStream.join(playersKTable, scorePlayerJoiner, joinSerdes);

        // 두번째 join: scoreWithPlayer * Product


        return null;
    }
}
