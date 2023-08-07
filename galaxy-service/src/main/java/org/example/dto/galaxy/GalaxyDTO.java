package org.example.dto.galaxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GalaxyDTO {
    @NotBlank
    protected String galaxyName;
    @NotBlank
    protected String galaxyDescription;
}
