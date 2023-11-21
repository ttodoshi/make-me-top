package org.example.galaxy.dto.starsystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetStarSystemWithDependenciesDto {
    private Integer systemId;
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    @JsonProperty("systemDependencyList")
    private List<SystemDependencyModelDto> dependencyList;
}
