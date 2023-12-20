package org.example.galaxy.dto.orbit;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateOrbitDto {
    @NotNull
    private Integer orbitLevel;
    @NotNull
    private Long galaxyId;
}
