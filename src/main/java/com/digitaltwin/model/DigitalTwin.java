package com.digitaltwin.model;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalTwin {
    private TurbineState desired;
    private TurbineState reported;
}
