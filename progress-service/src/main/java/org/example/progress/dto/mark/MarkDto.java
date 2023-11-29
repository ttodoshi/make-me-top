package org.example.progress.dto.mark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkDto {
    @NotNull
    private Long explorerId;
    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer value;
}
