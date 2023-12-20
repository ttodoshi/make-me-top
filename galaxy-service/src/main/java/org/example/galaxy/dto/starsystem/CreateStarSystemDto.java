package org.example.galaxy.dto.starsystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateStarSystemDto {
    @NotBlank
    @Size(max = 255)
    private String systemName;
    @NotNull
    private Integer systemPosition;
    @NotNull
    @Size(max = 255)
    private String description;
}
