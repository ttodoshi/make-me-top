package org.example.person.dto.person;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UpdatePersonDto {
    @NotNull
    private Integer maxExplorers;
}
