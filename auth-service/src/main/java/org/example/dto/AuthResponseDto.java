package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.dto.token.AccessTokenDto;
import org.example.dto.token.RefreshTokenDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private AccessTokenDto accessToken;
    private RefreshTokenDto refreshToken;
    private String role;
}
