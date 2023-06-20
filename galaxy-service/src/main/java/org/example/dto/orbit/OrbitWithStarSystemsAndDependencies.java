package org.example.dto.orbit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithoutOrbitId;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"orbitId", "galaxyId"})
public class OrbitWithStarSystemsAndDependencies extends OrbitDTO {
    private Integer orbitId;
    private List<StarSystemWithoutOrbitId> systemsList;
}
