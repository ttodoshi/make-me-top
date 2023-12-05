package org.example.homework.dto.homeworkmark;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CreateHomeworkMarkDto {
    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer value;
    private String comment;
}
