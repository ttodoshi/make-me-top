package org.example.person.dto.galaxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GalaxyDto {
    private Long galaxyId;
    private String galaxyName;
    private String galaxyDescription;
}
