package org.example.galaxy.dto.system;

import lombok.Data;

import java.util.List;

@Data
public class GetStarSystemWithDependenciesDto {
    private Long systemId;
    private String systemName;
    private Integer systemPosition;
    private Long orbitId;
    private List<SystemDependencyModelDto> systemDependencyList;
}
