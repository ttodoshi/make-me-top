package org.example.auth.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.auth.AuthResponseDto;
import org.example.auth.dto.token.RefreshTokenDto;
import org.example.auth.exception.token.FailedLogoutException;
import org.example.auth.exception.token.FailedRefreshException;
import org.example.auth.model.RefreshTokenInfo;
import org.example.auth.repository.RefreshTokenInfoRepository;
import org.example.auth.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenInfoRepository refreshTokenInfoRepository;

    @Override
    @Transactional(readOnly = true)
    public RefreshTokenInfo findRefreshTokenInfoByRefreshToken(String refreshToken) {
        return refreshTokenInfoRepository
                .findRefreshTokenInfoByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("refresh token not found");
                    return new FailedRefreshException();
                });
    }

    @Override
    @Transactional
    public void saveRefreshToken(Long personId, AuthResponseDto authResponse) {
        refreshTokenInfoRepository.save(
                new RefreshTokenInfo(
                        authResponse.getRefreshToken().getRefreshToken(),
                        personId,
                        authResponse.getRole(),
                        authResponse.getRefreshToken().getExpirationTime()
                )
        );
    }

    @Override
    @Transactional
    public void updateRefreshToken(RefreshTokenInfo refreshTokenInfo, RefreshTokenDto refreshToken) {
        refreshTokenInfo.setRefreshToken(refreshToken.getRefreshToken());
        refreshTokenInfo.setExpirationTime(refreshToken.getExpirationTime());
    }

    @Override
    @Transactional
    public void deleteRefreshTokenInfoByRefreshToken(String refreshToken) {
        if (refreshTokenInfoRepository.existsRefreshTokenInfoByRefreshToken(refreshToken)) {
            refreshTokenInfoRepository.deleteRefreshTokenInfoByRefreshToken(refreshToken);
        } else {
            log.warn("refresh token not found");
            throw new FailedLogoutException();
        }
    }
}
