package org.example.dto.keeper;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddKeeperRequest {
    @NotNull
    private Integer personId;
}
