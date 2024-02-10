package org.example.auth.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.auth.LoginRequestDto;
import org.example.auth.dto.mmtr.MmtrAuthResponseDto;
import org.example.auth.exception.connect.ConnectException;
import org.example.auth.exception.person.PersonNotFoundException;
import org.example.auth.service.AuthRequestSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthRequestSenderServiceImpl implements AuthRequestSenderService {
    private final WebClient.Builder webClientBuilder;

    @Value("${mmtr-url}")
    private String MMTR_AUTH_URL;

    @Override
    public MmtrAuthResponseDto sendAuthenticateRequest(LoginRequestDto loginRequest) {
        Optional<MmtrAuthResponseDto> responseOptional = webClientBuilder.baseUrl(MMTR_AUTH_URL).build()
                .post()
                .uri("ts-rest/SingleSignOn/authorization/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(MmtrAuthResponseDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> {
                    log.error("failed to authenticate when sending the request");
                    throw new ConnectException();
                }).blockOptional();
        if (responseOptional.isEmpty() || !responseOptional.get().getIsSuccess()) {
            log.debug("failed to authenticate when sending the request");
            throw new PersonNotFoundException();
        }
        return responseOptional.get();
    }
}
