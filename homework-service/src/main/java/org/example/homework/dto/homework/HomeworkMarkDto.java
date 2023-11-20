package org.example.homework.dto.homework;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HomeworkMarkDto {
    @NotNull
    private Integer explorerId;
    @NotNull
    private Integer value;
    private String comment;
}
