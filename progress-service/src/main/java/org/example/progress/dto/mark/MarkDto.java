package org.example.progress.dto.mark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkDto {
    @NotNull
    private Integer explorerId;
    @NotNull
    private Integer value;
}
