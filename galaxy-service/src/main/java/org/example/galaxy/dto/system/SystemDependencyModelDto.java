package org.example.galaxy.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemDependencyModelDto {
    private Long systemId;
    private String systemName;
    private String type;
    private Boolean isAlternative;
}
