package org.example.dto.orbit;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class OrbitDTO {
    private Integer orbitLevel;
    private Integer systemCount;
    private Integer galaxyId;
}
