package org.example.dto.orbit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class OrbitDTO {
    private Integer orbitLevel;
    private Integer systemCount;
    private Integer galaxyId;
}
