package org.example.dto.feedback;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CourseRatingCreateRequest {
    @NotNull
    private Integer rating;
}
