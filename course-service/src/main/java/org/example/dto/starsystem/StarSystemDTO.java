package org.example.dto.starsystem;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class StarSystemDTO {
    private Integer systemId;
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    private Integer orbitId;
}
