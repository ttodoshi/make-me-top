package org.example.planet.dto.planet;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreatePlanetDto {
    @NotBlank
    @Size(max = 255)
    private String planetName;
    @NotNull
    @Min(value = 1)
    private Integer planetNumber;
    @NotNull
    @Size(max = 255)
    private String description;
    @NotNull
    private String content;
}
