package org.example.dto.galaxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GalaxyDTO {
    private Integer galaxyId;
    private String galaxyName;
    private String galaxyDescription;
}
