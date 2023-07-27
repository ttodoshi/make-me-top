package org.example.dto.keeper;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class KeeperCreateRequest {
    @NotNull
    private Integer personId;
}
