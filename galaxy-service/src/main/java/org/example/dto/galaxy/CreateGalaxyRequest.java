package org.example.dto.galaxy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.orbit.CreateOrbitWithStarSystems;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Validated
public class CreateGalaxyRequest extends GalaxyDTO {
    @NotNull
    List<@Valid CreateOrbitWithStarSystems> orbitList;
}
