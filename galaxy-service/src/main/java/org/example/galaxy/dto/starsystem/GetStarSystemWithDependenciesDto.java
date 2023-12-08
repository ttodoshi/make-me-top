package org.example.galaxy.dto.starsystem;

import lombok.Data;

import java.util.List;

@Data
public class GetStarSystemWithDependenciesDto {
    private Long systemId;
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    private Long orbitId;
    private List<SystemDependencyModelDto> systemDependencyList;
}
