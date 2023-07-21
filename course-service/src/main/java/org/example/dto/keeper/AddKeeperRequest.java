package org.example.dto.keeper;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class AddKeeperRequest {
    private Integer personId;
}
