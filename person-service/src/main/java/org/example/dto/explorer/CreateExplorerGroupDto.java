package org.example.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExplorerGroupDto {
    @NotNull
    private Integer courseId;
    @NotNull
    private Integer keeperId;
}
