package org.example.service;

import org.example.config.security.JwtService;
import org.example.config.security.role.RoleChecker;
import org.example.dto.AuthResponseDto;
import org.example.dto.LoginRequestDto;
import org.example.dto.message.MessageDto;
import org.example.dto.mmtr.MmtrAuthResponseDto;
import org.example.dto.token.AccessTokenDto;
import org.example.dto.token.RefreshTokenDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.exception.classes.personEX.RoleNotAvailableException;
import org.example.exception.classes.tokenEX.FailedRefreshException;
import org.example.model.RefreshTokenInfo;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.RefreshTokenInfoRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Service
public class AuthService {
    private final RefreshTokenInfoRepository refreshTokenInfoRepository;
    private final PersonService personService;

    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;
    private final JwtService jwtGenerator;
    private final Map<String, RoleChecker> roleCheckerMap;

    @Value("${mmtr-auth-url}")
    private String MMTR_AUTH_URL;

    public AuthService(RefreshTokenInfoRepository refreshTokenInfoRepository, PersonService personService,
                       @Qualifier("authorizationHeaderRepository") AuthorizationHeaderRepository authorizationHeaderRepository,
                       @Qualifier("mmtrAuthorizationHeaderRepository") AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository,
                       WebClient.Builder webClientBuilder, JwtService jwtGenerator,
                       @Qualifier("roleCheckerMap") Map<String, RoleChecker> roleCheckerMap) {
        this.refreshTokenInfoRepository = refreshTokenInfoRepository;
        this.personService = personService;
        this.authorizationHeaderRepository = authorizationHeaderRepository;
        this.mmtrAuthorizationHeaderRepository = mmtrAuthorizationHeaderRepository;
        this.webClientBuilder = webClientBuilder;
        this.jwtGenerator = jwtGenerator;
        this.roleCheckerMap = roleCheckerMap;
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        MmtrAuthResponseDto authResponse = authenticatePerson(request);
        if (!isRoleAvailable(authResponse.getObject().getEmployeeId(), request.getRole()))
            throw new RoleNotAvailableException();
        AccessTokenDto accessToken = jwtGenerator.generateAccessToken(
                authResponse.getObject().getEmployeeId(),
                request.getRole()
        );
        RefreshTokenDto refreshToken = jwtGenerator.generateRefreshToken();
        cleanExpiredRefreshTokens();
        refreshTokenInfoRepository.save(
                new RefreshTokenInfo(
                        refreshToken.getRefreshToken(),
                        authResponse.getObject().getEmployeeId(),
                        request.getRole(),
                        refreshToken.getExpirationTime()
                )
        );
        authorizationHeaderRepository.setAuthorizationHeader("Bearer " + accessToken.getAccessToken());
        personService.savePersonIfNotExists(authResponse.getObject());
        return new AuthResponseDto(
                accessToken,
                refreshToken,
                request.getRole()
        );
    }

    private void cleanExpiredRefreshTokens() {
        refreshTokenInfoRepository.deleteAllByExpirationTimeBefore(new Date());
    }

    private MmtrAuthResponseDto authenticatePerson(LoginRequestDto loginRequestDto) {
        MmtrAuthResponseDto response = sendAuthenticateRequest(loginRequestDto);
        if (!response.getIsSuccess())
            throw new PersonNotFoundException();
        mmtrAuthorizationHeaderRepository.setAuthorizationHeader(
                "Bearer " + response.getObject().getUserToken().getTokenInfo()
        );
        return response;
    }

    private MmtrAuthResponseDto sendAuthenticateRequest(LoginRequestDto loginRequestDto) {
        return webClientBuilder.baseUrl(MMTR_AUTH_URL).build()
                .post()
                .uri("ts-rest/SingleSignOn/authorization/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestDto)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(MmtrAuthResponseDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> {
                    throw new ConnectException();
                })
                .block();
    }

    private boolean isRoleAvailable(Integer personId, String role) {
        return roleCheckerMap.containsKey(role) && roleCheckerMap.get(role).isRoleAvailable(personId);
    }

    @Transactional
    public AuthResponseDto refresh(String refreshTokenValue) {
        if (refreshTokenValue == null || !jwtGenerator.isRefreshTokenValid(refreshTokenValue))
            throw new FailedRefreshException();
        RefreshTokenInfo refreshTokenInfo = refreshTokenInfoRepository
                .findRefreshTokenInfoByRefreshToken(refreshTokenValue)
                .orElseThrow(FailedRefreshException::new);
        RefreshTokenDto newRefreshToken = jwtGenerator.generateRefreshToken();
        refreshTokenInfo.setRefreshToken(newRefreshToken.getRefreshToken());
        refreshTokenInfo.setExpirationTime(newRefreshToken.getExpirationTime());
        return new AuthResponseDto(
                jwtGenerator.generateAccessToken(
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
