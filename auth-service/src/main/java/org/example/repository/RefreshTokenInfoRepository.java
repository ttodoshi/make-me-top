package org.example.repository;

import org.example.model.RefreshTokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface RefreshTokenInfoRepository extends JpaRepository<RefreshTokenInfo, Integer> {
    Optional<RefreshTokenInfo> findRefreshTokenInfoByRefreshToken(String refreshToken);

    void deleteRefreshTokenInfoByRefreshToken(String refreshToken);

    void deleteAllByExpirationTimeBefore(Date date);
}
