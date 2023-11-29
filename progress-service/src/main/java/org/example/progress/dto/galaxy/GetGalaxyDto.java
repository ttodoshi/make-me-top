package org.example.progress.dto.galaxy;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.progress.dto.orbit.GetOrbitWithStarSystemsWithoutGalaxyIdDto;

import java.util.List;

@Data
public class GetGalaxyDto {
    private String galaxyName;
    private String galaxyDescription;
    private Long galaxyId;
    @JsonProperty("orbitList")
    private List<GetOrbitWithStarSystemsWithoutGalaxyIdDto> orbitList;
}
