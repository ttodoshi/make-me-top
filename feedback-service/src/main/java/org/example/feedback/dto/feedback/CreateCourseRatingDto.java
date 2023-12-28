package org.example.feedback.dto.feedback;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CreateCourseRatingDto {
    @NotNull
    private Long explorerId;
    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;
}
