package org.example.dto.orbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrbitWithStarSystemsGetResponse extends OrbitDTO {
    private Integer orbitId;
    @JsonProperty("systemList")
    List<StarSystemWithDependenciesGetResponse> systemWithDependenciesList;
}
