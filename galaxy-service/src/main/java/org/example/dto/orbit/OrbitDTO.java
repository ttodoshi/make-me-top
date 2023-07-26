package org.example.dto.orbit;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrbitDTO {
    @NotNull
    private Integer orbitLevel;
    @NotNull
    private Integer systemCount;
    @NotNull
    private Integer galaxyId;
}
