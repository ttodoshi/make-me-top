package org.example.dto;

import lombok.Data;

@Data
public class TokenInfoDto {
    private Long expiration;
    private Boolean isDeleted;
    private Integer employeeId;
    private String tokenInfo;
    private Integer tokenId;
}
