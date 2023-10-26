package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.KeeperDto;
import org.example.dto.person.PersonWithSystemsDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.model.StarSystem;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.service.KeeperService;
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
public class KeeperServiceImpl implements KeeperService {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<PersonWithSystemsDto> getKeepersWithSystems(Map<Integer, List<KeeperDto>> keepers, List<StarSystem> systems) {
        return systems.stream()
                .flatMap(s ->
                        keepers.getOrDefault(s.getSystemId(), Collections.emptyList())
                                .stream()
                                .map(k -> Map.entry(k, s.getSystemId())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .map(k -> new PersonWithSystemsDto(k.getKey().getPersonId(), k.getKey().getFirstName(), k.getKey().getLastName(), k.getKey().getPatronymic(), k.getKey().getRating(), k.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, List<KeeperDto>> findKeepersWithCourseIds() {
        return webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri("keepers/all/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, List<KeeperDto>>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .blockLast();
    }
}
