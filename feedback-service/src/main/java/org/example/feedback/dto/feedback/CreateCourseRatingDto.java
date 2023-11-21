package org.example.feedback.dto.feedback;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateCourseRatingDto {
    @NotNull
    private Integer rating;
}
