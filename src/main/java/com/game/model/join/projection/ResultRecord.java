package com.game.model.join.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.model.Product;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultRecord implements Comparable<ResultRecord>{

    @JsonProperty("player_id")
    private Long playerId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("player_name")
    private String playerName;

    @JsonProperty("game_name")
    private String gameName;

    private Double score;

    public ResultRecord(ScoreWithPlayer scoreWithPlayer, Product product){
        this.playerId = scoreWithPlayer.getPlayer().getId();
        this.productId = product.getId();
        this.playerName = scoreWithPlayer.getPlayer().getName();
        this.gameName = product.getName();
        this.score = scoreWithPlayer.getScoreEvent().getScore();
    }

    @Override
    public int compareTo(ResultRecord o) {
        return Double.compare(o.score, this.score); // 내림차순
    }
}

