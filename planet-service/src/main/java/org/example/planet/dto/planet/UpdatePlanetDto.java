package org.example.planet.dto.planet;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdatePlanetDto {
    @NotBlank
    private String planetName;
    @NotNull
    private Integer planetNumber;
    @NotNull
    private Integer systemId;
}
