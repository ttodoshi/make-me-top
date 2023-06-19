package org.example.dto.dependency;

import lombok.Data;

@Data
public class DependencyDTO {
    private Integer childId;
    private Integer parentId;
    private Boolean isAlternative;
}
