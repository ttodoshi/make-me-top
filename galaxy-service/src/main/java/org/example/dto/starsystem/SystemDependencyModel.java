package org.example.dto.starsystem;

import lombok.Data;

@Data
public class SystemDependencyModel {
    private Integer systemId;
    private String type;
    private Boolean isAlternative;
}
