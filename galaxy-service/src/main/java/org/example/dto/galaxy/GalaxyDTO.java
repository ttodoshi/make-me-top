package org.example.dto.galaxy;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GalaxyDTO {
    @NotBlank
    private String galaxyName;
}
