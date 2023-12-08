package org.example.planet.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanetUpdateEvent {
    @NotBlank
    @Size(max = 255)
    private String planetName;
    @NotNull
    @Min(value = 1)
    private Integer planetNumber;
    @NotNull
    private Long systemId;
}
