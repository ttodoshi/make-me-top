package org.example.dto.starsystem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.dependency.DependencyGetInfoModel;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties({"orbitId", "systemId"})
public class StarSystemWithDependencies extends StarSystemDTO {
    private Integer systemId;
    @JsonProperty("systemDependencyList")
    private List<DependencyGetInfoModel> dependencyList;
}
