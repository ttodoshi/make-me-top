package org.example.homework.dto.homeworkrequest;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateHomeworkRequestDto {
    @NotBlank
    private String content;
}
