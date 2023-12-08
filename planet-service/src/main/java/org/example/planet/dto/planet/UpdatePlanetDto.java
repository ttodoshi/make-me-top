package org.example.planet.dto.planet;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdatePlanetDto {
    @NotBlank
    @Size(max = 255)
    private String planetName;
    @NotNull
    @Min(value = 1)
    private Integer planetNumber;
    @NotNull
    private Long systemId;
}
