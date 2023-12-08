package org.example.auth.service.implementations;

import org.example.auth.dto.auth.LoginRequestDto;
import org.example.auth.dto.mmtr.MmtrAuthResponseDto;
import org.example.auth.exception.classes.connect.ConnectException;
import org.example.auth.exception.classes.person.PersonNotFoundException;
import org.example.auth.service.AuthRequestSenderService;
import org.example.auth.utils.AuthorizationHeaderContextHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class AuthRequestSenderServiceImpl implements AuthRequestSenderService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderContextHolder mmtrAuthorizationHeaderContextHolder;

    @Value("${mmtr-url}")
    private String MMTR_AUTH_URL;

    public AuthRequestSenderServiceImpl(WebClient.Builder webClientBuilder,
                                        @Qualifier("mmtrAuthorizationHeaderContextHolder") AuthorizationHeaderContextHolder mmtrAuthorizationHeaderContextHolder) {
        this.webClientBuilder = webClientBuilder;
        this.mmtrAuthorizationHeaderContextHolder = mmtrAuthorizationHeaderContextHolder;
    }

    @Override
    public MmtrAuthResponseDto sendAuthenticateRequest(LoginRequestDto loginRequest) {
        MmtrAuthResponseDto response = webClientBuilder.baseUrl(MMTR_AUTH_URL).build()
                .post()
                .uri("ts-rest/SingleSignOn/authorization/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(MmtrAuthResponseDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> {
                    throw new ConnectException();
                }).block();
        if (!response.getIsSuccess())
            throw new PersonNotFoundException();
        mmtrAuthorizationHeaderContextHolder.setAuthorizationHeader(
                "Bearer " + response.getObject().getUserToken().getTokenInfo()
        );
        return response;
    }
}
