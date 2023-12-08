package org.example.galaxy.dto.dependency;

import lombok.Data;

@Data
public class SystemDependencyDto {
    private Long dependencyId;
    private Long childId;
    private Long parentId;
    private Boolean isAlternative;
}
