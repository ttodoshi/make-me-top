package org.example.auth.dto.mmtr;

import lombok.Data;

@Data
public class MmtrTokenInfoDto {
    private Long expiration;
    private Boolean isDeleted;
    private Long employeeId;
    private String tokenInfo;
    private Long tokenId;
}
