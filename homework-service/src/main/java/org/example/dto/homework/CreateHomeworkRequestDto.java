package org.example.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateHomeworkRequestDto {
    @NotBlank
    private String content;
}
