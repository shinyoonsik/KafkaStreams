package com.game.model.join.projection;

import com.game.model.Player;
import com.game.model.ScoreEvent;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreWithPlayer {
    private ScoreEvent scoreEvent;
    private Player player;
}
