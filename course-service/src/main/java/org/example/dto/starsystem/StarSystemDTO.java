package org.example.dto.starsystem;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StarSystemDTO {
    @NotNull
    private Integer systemId;
    @NotNull
    private String systemName;
    @NotNull
    private Integer systemLevel;
    @NotNull
    private Integer systemPosition;
    @NotNull
    private Integer orbitId;
}
