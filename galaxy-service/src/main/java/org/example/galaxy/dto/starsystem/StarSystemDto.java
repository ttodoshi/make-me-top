package org.example.galaxy.dto.starsystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StarSystemDto {
    @NotBlank
    private String systemName;
    @NotNull
    private Integer systemLevel;
    @NotNull
    private Integer systemPosition;
    @NotNull
    private Long orbitId;
}