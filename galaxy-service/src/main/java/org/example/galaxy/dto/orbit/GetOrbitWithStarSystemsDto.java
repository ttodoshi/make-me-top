package org.example.galaxy.dto.orbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.galaxy.dto.system.GetStarSystemWithDependenciesDto;

import java.util.List;

@Data
public class GetOrbitWithStarSystemsDto {
    private Long orbitId;
    private Integer orbitLevel;
    private Long galaxyId;
    @JsonProperty("systemList")
    private List<GetStarSystemWithDependenciesDto> systemWithDependenciesList;
}
