package com.game.model.join;

import com.game.model.Player;
import com.game.model.ScoreEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class ScoreWithPlayer {
    private ScoreEvent scoreEvent;
    private Player player;
}
