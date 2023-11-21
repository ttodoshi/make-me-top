package org.example.galaxy.dto.orbit;

import lombok.Data;
import org.example.galaxy.dto.starsystem.CreateStarSystemDto;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Validated
public class CreateOrbitWithStarSystemsDto {
    @NotNull
    private Integer orbitLevel;
    @NotNull
    private Integer systemCount;
    @NotNull
    private List<@Valid CreateStarSystemDto> systemList;
}
