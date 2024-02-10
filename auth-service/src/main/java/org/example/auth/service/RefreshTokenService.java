package org.example.auth.service;

import org.example.auth.dto.auth.AuthResponseDto;
import org.example.auth.dto.token.RefreshTokenDto;
import org.example.auth.model.RefreshTokenInfo;

public interface RefreshTokenService {
    RefreshTokenInfo findRefreshTokenInfoByRefreshToken(String refreshToken);

    void saveRefreshToken(Long personId, AuthResponseDto authResponse);

    void updateRefreshToken(RefreshTokenInfo refreshTokenInfo, RefreshTokenDto refreshToken);

    void deleteRefreshTokenInfoByRefreshToken(String refreshToken);
}
