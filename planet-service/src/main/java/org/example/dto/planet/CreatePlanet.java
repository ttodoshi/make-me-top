package org.example.dto.planet;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreatePlanet {
    @NotBlank
    private String planetName;
    @NotNull
    private Integer planetNumber;
    @NotNull
    private String description;
    @NotNull
    private String content;
    @NotNull
    private Integer systemId;
}
