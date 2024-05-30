package com.game.service;

import com.game.model.HighScores;
import com.game.model.Player;
import com.game.model.join.projection.ResultRecord;
import io.javalin.Javalin;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class GameBoardService {

    private final HostInfo hostInfo;
    private final KafkaStreams kafkaStreams;

    private ReadOnlyKeyValueStore<String, Player> getPlayerStore() {
        return this.kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        "player-state-store",
                        QueryableStoreTypes.keyValueStore())
        );
    }

    private ReadOnlyKeyValueStore<String, HighScores> getHighScoreStore() {
        return this.kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        "Top3-score-game-boards",
                        // state store type
                        QueryableStoreTypes.keyValueStore())
        );
    }

    public void start() {
        Javalin app = Javalin.create().start(hostInfo.port());

        app.get("/game-board", this::getHighScoreAll);
        app.get("/players", this::getPlayerAll);
    }

    public void getHighScoreAll(Context ctx) {
        Map<String, List<ResultRecord>> leaderboard = new HashMap<>();
        KeyValueIterator<String, HighScores> range = getHighScoreStore().all();

        while (range.hasNext()) {
            KeyValue<String, HighScores> next = range.next();
            String game = next.key;
            HighScores highScores = next.value;
            leaderboard.put(game, highScores.toList());
        }
        // close the iterator to avoid memory leaks!
        range.close();
        // return a JSON response
        ctx.json(leaderboard);
    }

    public void getPlayerAll(Context ctx) {
        Map<String, Player> returnMap = new HashMap<>();
        KeyValueIterator<String, Player> range = getPlayerStore().all();

        while (range.hasNext()) {
            KeyValue<String, Player> next = range.next();
            String key = next.key;
            Player value = next.value;
            returnMap.put(key, value);
        }
        // close the iterator to avoid memory leaks!
        range.close();
        // return a JSON response
        ctx.json(returnMap);
    }
}
