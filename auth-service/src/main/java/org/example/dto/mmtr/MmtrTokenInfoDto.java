package org.example.dto.mmtr;

import lombok.Data;

@Data
public class MmtrTokenInfoDto {
    private Long expiration;
    private Boolean isDeleted;
    private Integer employeeId;
    private String tokenInfo;
    private Integer tokenId;
}
