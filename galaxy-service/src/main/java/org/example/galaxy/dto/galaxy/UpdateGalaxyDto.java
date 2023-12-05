package org.example.galaxy.dto.galaxy;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UpdateGalaxyDto {
    @NotBlank
    @Size(max = 255)
    protected String galaxyName;
    @NotBlank
    protected String galaxyDescription;
}
