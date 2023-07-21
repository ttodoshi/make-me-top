package org.example.dto.dependency;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateDependencyRequest extends DependencyRequest {
    @NotNull
    private Boolean isAlternative;
}
