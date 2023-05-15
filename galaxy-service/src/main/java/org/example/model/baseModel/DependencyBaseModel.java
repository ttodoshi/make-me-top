package org.example.model.baseModel;

import lombok.Data;

@Data
public abstract class DependencyBaseModel {
    private Integer dependencyId;
    private Integer childId;
    private Integer parentId;
    private Boolean isAlternative;
}
