package org.example.galaxy.dto.dependency;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DependencyDto {
    @NotNull
    private Long childId;
    private Long parentId;
}
