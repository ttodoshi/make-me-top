package org.example.auth.repository;

import org.example.auth.model.RefreshTokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenInfoRepository extends JpaRepository<RefreshTokenInfo, Long> {
    Optional<RefreshTokenInfo> findRefreshTokenInfoByRefreshToken(String refreshToken);

    boolean existsRefreshTokenInfoByRefreshToken(String refreshToken);

    void deleteRefreshTokenInfoByRefreshToken(String refreshToken);
}
