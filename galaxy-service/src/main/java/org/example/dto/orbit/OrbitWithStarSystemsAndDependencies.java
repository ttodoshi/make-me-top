package org.example.dto.orbit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithDependencies;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"galaxyId", "orbitId"})
public class OrbitWithStarSystemsAndDependencies extends OrbitDTO {
    private Integer orbitId;
    private List<StarSystemDTO> systemsList;
}
