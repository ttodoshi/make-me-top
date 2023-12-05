package org.example.galaxy.dto.orbit;

import lombok.Data;

@Data
public class OrbitDto {
    private Long orbitId;
    private Integer orbitLevel;
    private Integer systemCount;
    private Long galaxyId;
}
