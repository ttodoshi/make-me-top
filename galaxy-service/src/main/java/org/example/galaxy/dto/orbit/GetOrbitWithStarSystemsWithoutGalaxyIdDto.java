package org.example.galaxy.dto.orbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.galaxy.dto.starsystem.GetStarSystemWithDependenciesDto;

import java.util.List;

@Data
public class GetOrbitWithStarSystemsWithoutGalaxyIdDto {
    private Integer orbitId;
    private Integer orbitLevel;
    private Integer systemCount;
    @JsonProperty("systemList")
    List<GetStarSystemWithDependenciesDto> systemWithDependenciesList;
}
