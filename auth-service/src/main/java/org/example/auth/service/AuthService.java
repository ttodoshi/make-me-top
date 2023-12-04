package org.example.auth.service;

import org.example.auth.config.security.JwtService;
import org.example.auth.config.security.role.RoleChecker;
import org.example.auth.dto.auth.AuthResponseDto;
import org.example.auth.dto.auth.LoginRequestDto;
import org.example.auth.dto.message.MessageDto;
import org.example.auth.dto.mmtr.MmtrAuthResponseDto;
import org.example.auth.dto.token.AccessTokenDto;
import org.example.auth.dto.token.RefreshTokenDto;
import org.example.auth.exception.classes.person.RoleNotAvailableException;
import org.example.auth.exception.classes.token.FailedRefreshException;
import org.example.auth.model.RefreshTokenInfo;
import org.example.auth.repository.RefreshTokenInfoRepository;
import org.example.auth.utils.AuthorizationHeaderContextHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthService {
    private final RefreshTokenInfoRepository refreshTokenInfoRepository;
    private final PersonService personService;
    private final AuthRequestSenderService authRequestSenderService;

    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;
    private final JwtService jwtService;
    private final Map<String, RoleChecker> roleCheckerMap;

    public AuthService(RefreshTokenInfoRepository refreshTokenInfoRepository, PersonService personService,
                       AuthRequestSenderService authRequestSenderService,
                       @Qualifier("authorizationHeaderContextHolder") AuthorizationHeaderContextHolder authorizationHeaderContextHolder,
                       JwtService jwtService,
                       @Qualifier("roleCheckerMap") Map<String, RoleChecker> roleCheckerMap) {
        this.refreshTokenInfoRepository = refreshTokenInfoRepository;
        this.personService = personService;
        this.authRequestSenderService = authRequestSenderService;
        this.authorizationHeaderContextHolder = authorizationHeaderContextHolder;
        this.jwtService = jwtService;
        this.roleCheckerMap = roleCheckerMap;
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        MmtrAuthResponseDto authResponse = authRequestSenderService
                .sendAuthenticateRequest(loginRequest);

        if (!isRoleAvailable(authResponse.getObject().getEmployeeId(), loginRequest.getRole()))
            throw new RoleNotAvailableException();

        AccessTokenDto accessToken = jwtService.generateAccessToken(
                authResponse.getObject().getEmployeeId(),
                loginRequest.getRole()
        );
        RefreshTokenDto refreshToken = jwtService.generateRefreshToken(
                authResponse.getObject().getEmployeeId()
        );

        refreshTokenInfoRepository.save(
                new RefreshTokenInfo(
                        refreshToken.getRefreshToken(),
                        authResponse.getObject().getEmployeeId(),
                        loginRequest.getRole(),
                        refreshToken.getExpirationTime()
                )
        );

        authorizationHeaderContextHolder.setAuthorizationHeader("Bearer " + accessToken.getAccessToken());
        personService.savePersonIfNotExists(authResponse.getObject());

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                loginRequest.getRole()
        );
    }

    private boolean isRoleAvailable(Long personId, String role) {
        return roleCheckerMap.containsKey(role) && roleCheckerMap.get(role).isRoleAvailable(personId);
    }

    @Transactional
    public AuthResponseDto refresh(String refreshTokenValue) {
        if (refreshTokenValue == null || !jwtService.isRefreshTokenValid(refreshTokenValue))
            throw new FailedRefreshException();

        RefreshTokenInfo refreshTokenInfo = refreshTokenInfoRepository
                .findRefreshTokenInfoByRefreshToken(refreshTokenValue)
                .orElseThrow(FailedRefreshException::new);

        RefreshTokenDto newRefreshToken = jwtService.generateRefreshToken(
                refreshTokenInfo.getPersonId()
        );
        refreshTokenInfo.setRefreshToken(newRefreshToken.getRefreshToken());
        refreshTokenInfo.setExpirationTime(newRefreshToken.getExpirationTime());

        return new AuthResponseDto(
                jwtService.generateAccessToken(
                        refreshTokenInfo.getPersonId(),
                        refreshTokenInfo.getRole()
                ),
                newRefreshToken,
                refreshTokenInfo.getRole()
        );
    }

    @Transactional
    public MessageDto logout(String refreshToken) {
        refreshTokenInfoRepository.deleteRefreshTokenInfoByRefreshToken(refreshToken);
        return new MessageDto("Выход успешный");
    }
}
