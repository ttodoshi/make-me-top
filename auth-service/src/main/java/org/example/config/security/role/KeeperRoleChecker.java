package org.example.config.security.role;

import org.example.dto.AuthResponseEmployeeDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.repository.AuthorizationHeaderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class KeeperRoleChecker implements RoleChecker {
    private final AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;
    @Value("${mmtr-auth-url}")
    private String MMTR_AUTH_URL;

    public KeeperRoleChecker(@Qualifier("mmtrAuthorizationHeaderRepository") AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository,
                             @Qualifier("webClientBuilder") WebClient.Builder webClientBuilder) {
        this.mmtrAuthorizationHeaderRepository = mmtrAuthorizationHeaderRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public boolean isRoleAvailable(Integer personId) {
        return webClientBuilder
                .baseUrl(MMTR_AUTH_URL).build()
                .get()
                .uri("ts-rest/coaching/getProgramsMentorsByFilters/")
                .header("Authorization", mmtrAuthorizationHeaderRepository
                        .getAuthorizationHeader())
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToFlux(AuthResponseEmployeeDto.class)
                .timeout(Duration.ofSeconds(5))
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
