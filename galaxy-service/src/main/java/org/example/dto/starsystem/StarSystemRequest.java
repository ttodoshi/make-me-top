package org.example.dto.starsystem;

import lombok.Data;

@Data
public class StarSystemRequest {
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    private String description;
    private Integer orbitId;
}
