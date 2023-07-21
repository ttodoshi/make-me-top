package org.example.dto.starsystem;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetStarSystem extends StarSystemDTO {
    private Integer systemId;
}
