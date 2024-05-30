package com.game.model;

import com.game.model.join.projection.ResultRecord;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.TreeSet;

@Data
@AllArgsConstructor
public class HighScores {
    private final TreeSet<ResultRecord> highScores = new TreeSet<>();

    public HighScores add(final ResultRecord resultRecord){
        this.highScores.add(resultRecord);

        if(this.highScores.size() > 3){
            highScores.remove(highScores.last());
        }

        return this;
    }

    public List<ResultRecord> toList(){
        return this.highScores.stream().toList();
    }
}
