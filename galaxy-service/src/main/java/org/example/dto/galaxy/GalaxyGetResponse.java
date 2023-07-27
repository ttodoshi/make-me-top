package org.example.dto.galaxy;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.orbit.OrbitWithStarSystemsWithoutGalaxyIdGetResponse;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties("galaxyDescription")
public class GalaxyGetResponse extends GalaxyDTO {
    private Integer galaxyId;
    @JsonProperty("orbitList")
    private List<OrbitWithStarSystemsWithoutGalaxyIdGetResponse> orbitList;
}
