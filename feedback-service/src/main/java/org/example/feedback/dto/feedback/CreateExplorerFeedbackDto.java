package org.example.feedback.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExplorerFeedbackDto {
    @NotNull
    private Integer keeperId;
    @NotNull
    private Integer rating;
    @NotBlank
    private String comment;
}
