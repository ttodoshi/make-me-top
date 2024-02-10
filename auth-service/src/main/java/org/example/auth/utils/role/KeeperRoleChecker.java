package org.example.auth.utils.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.mmtr.MmtrAuthResponseEmployeeDto;
import org.example.auth.enums.AuthenticationRoleType;
import org.example.auth.exception.connect.ConnectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeeperRoleChecker implements RoleChecker {
    private final WebClient.Builder webClientBuilder;
    @Value("${mmtr-url}")
    private String MMTR_AUTH_URL;

    @Override
    public boolean isRoleAvailable(Long personId, String mmtrUserToken) {
        return webClientBuilder
                .baseUrl(MMTR_AUTH_URL).build()
                .get()
                .uri("ts-rest/coaching/getProgramsMentorsByFilters/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + mmtrUserToken)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToFlux(MmtrAuthResponseEmployeeDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(new AccessDeniedException(
                                "Вам закрыт доступ к данной функциональности бортового компьютера"
                        ))
                ).collectList()
                .blockOptional()
                .orElseThrow(() -> {
                    log.error("failed to request curators list");
                    return new ConnectException();
                })
                .stream().anyMatch(
                        c -> c.getEmployeeId().equals(personId)
                );
    }

    @Override
    public String getType() {
        return AuthenticationRoleType.KEEPER.name();
    }
}
