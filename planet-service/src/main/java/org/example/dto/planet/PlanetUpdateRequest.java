package org.example.dto.planet;

import lombok.Data;

@Data
public class PlanetUpdateRequest {
    private String planetName;
    private Integer planetNumber;
    private Integer systemId;
}
