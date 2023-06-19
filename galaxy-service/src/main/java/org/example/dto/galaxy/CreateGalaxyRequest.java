package org.example.dto.galaxy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.orbit.OrbitWithStarSystemsAndDependencies;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateGalaxyRequest extends GalaxyDTO {
    List<OrbitWithStarSystemsAndDependencies> orbitsList;
}
