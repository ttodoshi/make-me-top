package org.example.dto.starsystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemDependencyModelDto {
    private Integer systemId;
    private String type;
    private Boolean isAlternative;
}
