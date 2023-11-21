package org.example.galaxy.dto.orbit;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrbitDto {
    @NotNull
    private Integer orbitLevel;
    @NotNull
    private Integer systemCount;
    @NotNull
    private Integer galaxyId;
}
