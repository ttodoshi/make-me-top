package org.example.service;

import org.example.config.security.JwtService;
import org.example.config.security.role.RoleChecker;
import org.example.dto.AuthResponseDto;
import org.example.dto.LoginRequestDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.exception.classes.personEX.RoleNotAvailableException;
import org.example.repository.AuthorizationHeaderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final PersonService personService;

    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;
    private final JwtService jwtGenerator;
    private final Map<String, RoleChecker> roleCheckerMap;

    @Value("${mmtr-auth-url}")
    private String MMTR_AUTH_URL;

    public AuthService(PersonService personService,
                       @Qualifier("authorizationHeaderRepository") AuthorizationHeaderRepository authorizationHeaderRepository,
                       @Qualifier("mmtrAuthorizationHeaderRepository") AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository,
                       @Qualifier("webClientBuilder") WebClient.Builder webClientBuilder, JwtService jwtGenerator,
                       @Qualifier("roleCheckerMap") Map<String, RoleChecker> roleCheckerMap) {
        this.personService = personService;
        this.authorizationHeaderRepository = authorizationHeaderRepository;
        this.mmtrAuthorizationHeaderRepository = mmtrAuthorizationHeaderRepository;
        this.webClientBuilder = webClientBuilder;
        this.jwtGenerator = jwtGenerator;
        this.roleCheckerMap = roleCheckerMap;
    }

    public String login(LoginRequestDto request, HttpServletResponse response) {
        AuthResponseDto authResponse = authenticatePerson(request);
        if (!isRoleAvailable(authResponse.getObject().getEmployeeId(), request.getRole()))
            throw new RoleNotAvailableException();
        String token = jwtGenerator.generateToken(authResponse.getObject().getEmployeeId(), request.getRole());
        authorizationHeaderRepository.setAuthorizationHeader("Bearer " + token);
        personService.savePersonIfNotExists(authResponse.getObject());
        Cookie tokenCookie = generateCookie(token);
        response.addCookie(tokenCookie);
        return token;
    }

    private AuthResponseDto authenticatePerson(LoginRequestDto loginRequestDto) {
        AuthResponseDto response = sendAuthenticateRequest(loginRequestDto);
        if (!response.getIsSuccess())
            throw new PersonNotFoundException();
        mmtrAuthorizationHeaderRepository.setAuthorizationHeader(
                "Bearer " + response.getObject().getUserToken().getTokenInfo()
        );
        return response;
    }

    private AuthResponseDto sendAuthenticateRequest(LoginRequestDto loginRequestDto) {
        return webClientBuilder.baseUrl(MMTR_AUTH_URL).build()
                .post()
                .uri("ts-rest/SingleSignOn/authorization/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestDto)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(AuthResponseDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> {
                    throw new ConnectException();
                })
                .block();
    }

    private boolean isRoleAvailable(Integer personId, String role) {
        return roleCheckerMap.containsKey(role) && roleCheckerMap.get(role).isRoleAvailable(personId);
    }

    private Cookie generateCookie(String token) {
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setMaxAge(43200);
        tokenCookie.setPath("/");
        return tokenCookie;
    }

    public Map<String, Object> logout(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie("token", "");
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("message", "Выход успешный");
        return jsonResponse;
    }
}
