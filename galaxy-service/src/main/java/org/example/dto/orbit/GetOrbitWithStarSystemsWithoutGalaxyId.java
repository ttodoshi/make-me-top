package org.example.dto.orbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.dto.starsystem.GetStarSystemWithDependencies;

import java.util.List;

@Data
public class GetOrbitWithStarSystemsWithoutGalaxyId {
    private Integer orbitId;
    private Integer orbitLevel;
    private Integer systemCount;
    @JsonProperty("systemList")
    List<GetStarSystemWithDependencies> systemWithDependenciesList;
}
