package org.example.dto.person;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePersonDto {
    @NotNull
    private Integer maxExplorers;
}
