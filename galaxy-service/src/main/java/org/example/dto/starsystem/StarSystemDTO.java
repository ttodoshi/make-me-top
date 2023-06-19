package org.example.dto.starsystem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class StarSystemDTO {
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    private Integer orbitId;
}
