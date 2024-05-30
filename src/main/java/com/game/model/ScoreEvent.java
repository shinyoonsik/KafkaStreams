package com.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreEvent {

    @JsonProperty("player_id")
    private Long playerId;

    @JsonProperty("product_id")
    private Long productId;

    private Double score;

    @JsonCreator
    public ScoreEvent(@JsonProperty("product_id") Long productId,
                      @JsonProperty("player_id") Long playerId,
                      @JsonProperty("score") Double score)
    {
        this.productId = productId;
        this.playerId = playerId;
        this.score = score;
    }
}
