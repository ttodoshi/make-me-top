package org.example.galaxy.dto.starsystem;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateStarSystemDto {
    @NotBlank
    @Max(value = 255)
    private String systemName;
    @NotNull
    private Integer systemPosition;
    @NotNull
    private Long orbitId;
}
