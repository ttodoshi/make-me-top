package org.example.auth.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.dto.token.AccessTokenDto;
import org.example.auth.dto.token.RefreshTokenDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private AccessTokenDto accessToken;
    private RefreshTokenDto refreshToken;
    private String role;
}
