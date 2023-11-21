package org.example.galaxy.dto.galaxy;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.galaxy.dto.orbit.GetOrbitWithStarSystemsWithoutGalaxyIdDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetGalaxyDto extends GalaxyDto {
    private Integer galaxyId;
    @JsonProperty("orbitList")
    private List<GetOrbitWithStarSystemsWithoutGalaxyIdDto> orbitList;
}
