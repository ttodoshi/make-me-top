package org.example.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerFeedbackCreateRequest {
    @NotNull
    private Integer keeperId;
    @NotNull
    private Integer rating;
    @NotBlank
    private String comment;
}
