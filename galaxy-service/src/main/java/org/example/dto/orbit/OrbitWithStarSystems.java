package org.example.dto.orbit;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.starsystem.StarSystemWithDependencies;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties("orbitId")
public class OrbitWithStarSystems extends OrbitDTO {
    private Integer orbitId;
    @JsonProperty("systemList")
    List<StarSystemWithDependencies> systemWithDependenciesList;
}
