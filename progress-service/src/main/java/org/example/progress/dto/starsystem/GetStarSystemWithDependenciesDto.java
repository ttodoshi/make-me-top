package org.example.progress.dto.starsystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetStarSystemWithDependenciesDto {
    private Long systemId;
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    @JsonProperty("systemDependencyList")
    private List<SystemDependencyModelDto> dependencyList;
}
