package org.example.dto.starsystem;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NotNull
public class CreateStarSystem extends CreateStarSystemWithoutOrbitId {
    private Integer orbitId;
}
