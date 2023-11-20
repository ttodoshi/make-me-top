package org.example.galaxy.dto.dependency;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateDependencyDto extends DependencyDto {
    @NotNull
    private Boolean isAlternative;
}
