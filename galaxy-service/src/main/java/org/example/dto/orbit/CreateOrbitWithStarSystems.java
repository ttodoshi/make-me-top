package org.example.dto.orbit;

import lombok.Data;
import org.example.dto.starsystem.CreateStarSystem;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NotNull
@Validated
public class CreateOrbitWithStarSystems {
    private Integer orbitLevel;
    private Integer systemCount;
    private List<@Valid CreateStarSystem> systemsList;
}
