package org.example.galaxy.dto.galaxy;

import lombok.Data;
import org.example.galaxy.dto.orbit.CreateOrbitWithStarSystemsDto;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Validated
public class CreateGalaxyDto {
    @NotBlank
    @Size(max = 255)
    private String galaxyName;
    @NotBlank
    private String galaxyDescription;
    @NotNull
    private List<@Valid CreateOrbitWithStarSystemsDto> orbitList;
}
