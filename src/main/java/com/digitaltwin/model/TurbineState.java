package com.digitaltwin.model;

import com.digitaltwin.model.Enum.Power;
import com.digitaltwin.model.Enum.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TurbineState {
    private String timestamp;

    @JsonProperty("wind_speed_mph")
    private Double windSpeedMph;
    private Power power;
    private Type type;

    public static TurbineState clone(TurbineState original) {
        return TurbineState.builder()
                .timestamp(original.getTimestamp())
                .windSpeedMph(original.getWindSpeedMph())
                .power(original.getPower())
                .type(original.getType())
                .build();
    }
}
