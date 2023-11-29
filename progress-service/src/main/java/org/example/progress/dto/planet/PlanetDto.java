package org.example.progress.dto.planet;


import lombok.Data;

@Data
public class PlanetDto {
    private Long planetId;
    private String planetName;
    private Integer planetNumber;
    private Long systemId;
}
