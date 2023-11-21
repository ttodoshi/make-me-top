package org.example.person.dto.planet;


import lombok.Data;

@Data
public class PlanetDto {
    private Integer planetId;
    private String planetName;
    private Integer planetNumber;
    private Integer systemId;
}
