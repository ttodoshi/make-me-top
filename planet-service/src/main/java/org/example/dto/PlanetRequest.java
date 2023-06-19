package org.example.dto;

import lombok.Data;

@Data
public class PlanetRequest {
    private String planetName;
    private Integer planetNumber;
    private Integer systemId;
}
