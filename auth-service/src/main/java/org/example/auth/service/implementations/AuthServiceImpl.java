package org.example.auth.service.implementations;

import lombok.extern.slf4j.Slf4j;
import org.example.auth.config.security.JwtService;
import org.example.auth.dto.auth.AuthResponseDto;
import org.example.auth.dto.auth.LoginRequestDto;
import org.example.auth.dto.message.MessageDto;
import org.example.auth.dto.mmtr.MmtrAuthResponseEmployeeDto;
import org.example.auth.dto.token.AccessTokenDto;
import org.example.auth.dto.token.RefreshTokenDto;
import org.example.auth.exception.person.RoleNotAvailableException;
import org.example.auth.exception.token.FailedRefreshException;
import org.example.auth.model.RefreshTokenInfo;
import org.example.auth.service.AuthRequestSenderService;
import org.example.auth.service.AuthService;
import org.example.auth.service.PersonService;
import org.example.auth.service.RefreshTokenService;
import org.example.auth.utils.role.RoleChecker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final PersonService personService;
    private final AuthRequestSenderService authRequestSenderService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final Map<String, RoleChecker> roleCheckerMap;

    public AuthServiceImpl(PersonService personService, AuthRequestSenderService authRequestSenderService,
                           RefreshTokenService refreshTokenService, JwtService jwtService,
                           @Qualifier("roleCheckerMap") Map<String, RoleChecker> roleCheckerMap) {
        this.personService = personService;
        this.authRequestSenderService = authRequestSenderService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.roleCheckerMap = roleCheckerMap;
    }

    @Override
    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        MmtrAuthResponseEmployeeDto employeeInfo = authRequestSenderService
                .sendAuthenticateRequest(loginRequest)
                .getObject();

        if (!isRoleAvailable(employeeInfo.getEmployeeId(), loginRequest.getRole(), employeeInfo.getUserToken().getTokenInfo())) {
            log.debug("role not available");
            throw new RoleNotAvailableException();
        }

        AuthResponseDto authResponse = generateAuthResponse(
                employeeInfo.getEmployeeId(), loginRequest.getRole()
        );

        refreshTokenService.saveRefreshToken(employeeInfo.getEmployeeId(), authResponse);
        personService.savePerson(employeeInfo);

        return authResponse;
    }

    private boolean isRoleAvailable(Long personId, String role, String mmtrUserToken) {
        return roleCheckerMap.containsKey(role) &&
                roleCheckerMap.get(role).isRoleAvailable(personId, mmtrUserToken);
    }

    private AuthResponseDto generateAuthResponse(Long personId, String role) {
        AccessTokenDto accessToken = jwtService.generateAccessToken(
                personId, role
        );
        RefreshTokenDto refreshToken = jwtService.generateRefreshToken(personId);
        return new AuthResponseDto(accessToken, refreshToken, role);
    }

    @Override
    @Transactional
    public AuthResponseDto refresh(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            log.warn("refresh token not valid");
            throw new FailedRefreshException();
        }

        RefreshTokenInfo refreshTokenInfo = refreshTokenService
                .findRefreshTokenInfoByRefreshToken(refreshToken);

        AuthResponseDto authResponse = generateAuthResponse(
                refreshTokenInfo.getPersonId(), refreshTokenInfo.getRole()
        );

        refreshTokenService.updateRefreshToken(refreshTokenInfo, authResponse.getRefreshToken());

        return authResponse;
    }

    @Override
    @Transactional
    public MessageDto logout(String refreshToken) {
        refreshTokenService.deleteRefreshTokenInfoByRefreshToken(refreshToken);
        return new MessageDto("Выход успешный");
    }
}
