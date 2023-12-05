package org.example.galaxy.dto.galaxy;


import lombok.Data;
import org.example.galaxy.dto.orbit.GetOrbitWithStarSystemsDto;

import java.util.List;

@Data
public class GetGalaxyDto {
    private Long galaxyId;
    private String galaxyName;
    private String galaxyDescription;
    private List<GetOrbitWithStarSystemsDto> orbitList;
}
