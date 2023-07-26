package org.example.dto.dependency;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DependencyRequest {
    @NotNull
    private Integer childId;
    @NotNull
    private Integer parentId;
}
