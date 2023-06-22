package org.example.dto.orbit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.starsystem.GetStarSystemWithDependencies;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties("galaxyId")
public class OrbitWithStarSystemsWithoutGalaxyId extends OrbitDTO {
    private Integer orbitId;
    @JsonProperty("systemList")
    List<GetStarSystemWithDependencies> systemWithDependenciesList;
}
