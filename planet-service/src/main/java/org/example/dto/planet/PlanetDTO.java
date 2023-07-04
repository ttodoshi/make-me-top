package org.example.dto.planet;

import lombok.Data;

@Data
public class PlanetDTO {
    private String planetName;
    private Integer planetNumber;
    private String description;
    private String content;
    private Integer systemId;
}
