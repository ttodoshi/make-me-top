package org.example.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateHomeworkFeedbackDto {
    @NotNull
    private Integer explorerId;
    @NotBlank
    private String content;
}
