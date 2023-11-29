package org.example.auth.config.security.role;

import org.example.auth.dto.mmtr.MmtrAuthResponseEmployeeDto;
import org.example.auth.enums.AuthenticationRoleType;
import org.example.auth.exception.classes.connect.ConnectException;
import org.example.auth.utils.AuthorizationHeaderContextHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class KeeperRoleChecker implements RoleChecker {
    private final AuthorizationHeaderContextHolder mmtrAuthorizationHeaderContextHolder;
    private final WebClient.Builder webClientBuilder;
    @Value("${mmtr-auth-url}")
    private String MMTR_AUTH_URL;

    public KeeperRoleChecker(@Qualifier("mmtrAuthorizationHeaderContextHolder") AuthorizationHeaderContextHolder mmtrAuthorizationHeaderContextHolder,
                             WebClient.Builder webClientBuilder) {
        this.mmtrAuthorizationHeaderContextHolder = mmtrAuthorizationHeaderContextHolder;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public boolean isRoleAvailable(Long personId) {
        return webClientBuilder
                .baseUrl(MMTR_AUTH_URL).build()
                .get()
                .uri("ts-rest/coaching/getProgramsMentorsByFilters/")
                .header("Authorization", mmtrAuthorizationHeaderContextHolder.getAuthorizationHeader())
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToFlux(MmtrAuthResponseEmployeeDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(throwable -> {
                    throw new ConnectException();
                })
                .collectList()
                .blockOptional()
                .orElseThrow(ConnectException::new)
                .stream().anyMatch(
                        c -> c.getEmployeeId().equals(personId)
                );
    }

    @Override
    public String getType() {
        return AuthenticationRoleType.KEEPER.name();
    }
}
