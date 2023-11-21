package org.example.person.dto.keeper;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateKeeperDto {
    @NotNull
    private Integer personId;
}
