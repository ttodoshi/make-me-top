package org.example.galaxy.dto.system;

import lombok.Data;

@Data
public class StarSystemDto {
    private Long systemId;
    private String systemName;
    private Integer systemPosition;
    private Long orbitId;
}
