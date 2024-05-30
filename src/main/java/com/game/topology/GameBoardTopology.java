package com.game.topology;

import com.game.model.HighScores;
import com.game.model.Player;
import com.game.model.Product;
import com.game.model.ScoreEvent;
import com.game.model.join.projection.ResultRecord;
import com.game.model.join.projection.ScoreWithPlayer;
import com.game.serdes.json.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

public class GameBoardTopology {

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // source processor
        KStream<String, ScoreEvent> scoreEventKStream = streamsBuilder
                .stream("score-events", Consumed.with(Serdes.ByteArray(), JsonSerdes.getScoreEventSerde()))
                .selectKey((k, v) -> v.getPlayerId().toString()) // 특정 조인을 수행할 때, 코-파티셔닝 요구사항을 만족시키기위해 필요
                .peek((key, value) -> System.out.println("scoreEventKStream: " + key + " => " + value)); // 디버깅을 위한 로그 출력

        // source processor
        KTable<String, Player> playersKTable = streamsBuilder
                .table("players",
                        Consumed.with(Serdes.String(), JsonSerdes.getPlayerSerde()),
                        Materialized.as("player-state-store"))
                .toStream()
                .peek((key, value) -> System.out.println("playersKTable: " + key + " => " + value)).toTable(); // 디버깅을 위한 로그 출력

        // source processor
        GlobalKTable<String, Product> productGlobalKTable = streamsBuilder
                .globalTable("products", Consumed.with(Serdes.String(), JsonSerdes.getProductSerde()));

        // join Serdes scoreEvents & players
        Joined<String, ScoreEvent, Player> joinSerdes = Joined.with(Serdes.String(), JsonSerdes.getScoreEventSerde(), JsonSerdes.getPlayerSerde());

        // Projection: ValueJoiner는 단순히 조인에 참여하는 각 레코드를 새롭게 결합된 레코드로 만드는 객체
        ValueJoiner<ScoreEvent, Player, ScoreWithPlayer> scorePlayerJoiner = ScoreWithPlayer::new;

        // 첫번째 join: ScoreEvent & Player
        KStream<String, ScoreWithPlayer> withPlayer = scoreEventKStream
                .join(playersKTable, scorePlayerJoiner, joinSerdes)
                .peek((key, value) -> System.out.println("첫번째 Joined: " + key + " => " + value));

        // KeyValueMapper: 조인하기 위해 Stream의 레코드에서 조인 키를 추출하거나 생성하는 데 사용. join시 productId가 key로 사용된다
        KeyValueMapper<String, ScoreWithPlayer, String> keyValueMapper =
                (key, value) -> String.valueOf(value.getScoreEvent().getProductId());

        // Projection: 두 번째 조인결과에 대한 프로젝션
        ValueJoiner<ScoreWithPlayer, Product, ResultRecord> resultRecordJoiner = ResultRecord::new;

        // 두번째 join: withPlayer * Product
        KStream<String, ResultRecord> withProduct = withPlayer.join(productGlobalKTable, keyValueMapper, resultRecordJoiner);
        withProduct.print(Printed.<String, ResultRecord>toSysOut().withLabel("with-products"));

        // 집계 연산전에 무조건 grouping이 필요!
        KGroupedStream<String, ResultRecord> groupedResultRecord = withProduct.groupBy(
                (key, value) -> value.getProductId().toString(),
                Grouped.with(Serdes.String(), JsonSerdes.getResultRecordSerde())
        );

        // 집계를 시작할 때, 초기화 값으로 사용될 함수
        Initializer<HighScores> highScoresInitializer = HighScores::new;

        // 실제 집계 연산을 수행할 함수
        Aggregator<String, ResultRecord, HighScores> highScoresAggr = (key, value, aggregate) -> aggregate.add(value);

        // 집계 상태를 저장할 저장소 설정
        Materialized<String, HighScores, KeyValueStore<Bytes, byte[]>> materialized = Materialized.<String, HighScores, KeyValueStore<Bytes, byte[]>>
                        as("Top3-score-game-boards")
                .withKeySerde(Serdes.String())
                .withValueSerde(JsonSerdes.getHighScoresSerde());

        // 집계 연산
        KTable<String, HighScores> aggregatedTable = groupedResultRecord.aggregate(
                highScoresInitializer,
                highScoresAggr,
                materialized);

        // sink processor
        aggregatedTable.toStream().to("high-scores");

        return streamsBuilder.build();
    }
}
