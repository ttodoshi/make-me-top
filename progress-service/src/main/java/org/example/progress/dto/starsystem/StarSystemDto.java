package org.example.progress.dto.starsystem;

import lombok.Data;

@Data
public class StarSystemDto {
    private Integer systemId;
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    private Integer orbitId;
}
