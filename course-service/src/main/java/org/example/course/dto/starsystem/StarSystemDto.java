package org.example.course.dto.starsystem;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StarSystemDto {
    @NotNull
    private Long systemId;
    @NotNull
    private String systemName;
    @NotNull
    private Integer systemLevel;
    @NotNull
    private Integer systemPosition;
    @NotNull
    private Long orbitId;
}
