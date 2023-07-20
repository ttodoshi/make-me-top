package org.example.dto.starsystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetStarSystemWithDependencies {
    private Integer systemId;
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    @JsonProperty("systemDependencyList")
    private List<SystemDependencyModel> dependencyList;
}
