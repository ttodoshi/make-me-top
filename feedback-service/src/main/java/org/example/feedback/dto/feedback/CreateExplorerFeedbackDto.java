package org.example.feedback.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExplorerFeedbackDto {
    @NotNull
    private Long explorerId;
    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;
    @NotBlank
    private String comment;
}
