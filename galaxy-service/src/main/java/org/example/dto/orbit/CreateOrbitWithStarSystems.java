package org.example.dto.orbit;

import lombok.Data;
import org.example.dto.starsystem.CreateStarSystem;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Validated
public class CreateOrbitWithStarSystems {
    @NotNull
    private Integer orbitLevel;
    @NotNull
    private Integer systemCount;
    @NotNull
    private List<@Valid CreateStarSystem> systemList;
}
