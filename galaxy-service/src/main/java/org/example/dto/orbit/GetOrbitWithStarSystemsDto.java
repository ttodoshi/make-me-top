package org.example.dto.orbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.starsystem.GetStarSystemWithDependenciesDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetOrbitWithStarSystemsDto extends OrbitDto {
    private Integer orbitId;
    @JsonProperty("systemList")
    List<GetStarSystemWithDependenciesDto> systemWithDependenciesList;
}
