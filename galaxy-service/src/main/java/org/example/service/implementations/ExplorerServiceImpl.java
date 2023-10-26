package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.person.PersonWithSystemsDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.model.StarSystem;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.service.ExplorerService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerServiceImpl implements ExplorerService {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<PersonWithSystemsDto> getExplorersWithSystems(Map<Integer, List<ExplorerDto>> explorers, List<StarSystem> systems) {
        return systems.stream()
                .flatMap(s ->
                        explorers.getOrDefault(s.getSystemId(), Collections.emptyList())
                                .stream()
                                .map(e -> Map.entry(e, s.getSystemId())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .map(e -> new PersonWithSystemsDto(e.getKey().getPersonId(), e.getKey().getFirstName(), e.getKey().getLastName(), e.getKey().getPatronymic(), e.getKey().getRating(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, List<ExplorerDto>> findExplorersWithCourseIds() {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri("explorers/all/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, List<ExplorerDto>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .blockLast();
    }
}
