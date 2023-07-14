package org.example.dto.planet;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull
public class PlanetUpdateRequest {
    @NotBlank
    private String planetName;
    private Integer planetNumber;
    private Integer systemId;
}
