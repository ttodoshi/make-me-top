package org.example.galaxy.dto.orbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.galaxy.dto.starsystem.GetStarSystemWithDependenciesDto;

import java.util.List;

@Data
public class GetOrbitWithStarSystemsDto {
    private Long orbitId;
    private Integer orbitLevel;
    private Integer systemCount;
    private Long galaxyId;
    @JsonProperty("systemList")
    private List<GetStarSystemWithDependenciesDto> systemWithDependenciesList;
}
