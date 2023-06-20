package org.example.dto.starsystem;

import lombok.Data;

@Data
public class StarSystemDTO {
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    private Integer orbitId;
}
