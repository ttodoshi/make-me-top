package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.course.CourseGetResponse;
import org.example.dto.explorer.ExplorerWithSystemsDTO;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.dto.keeper.KeeperWithSystemsDTO;
import org.example.model.Galaxy;
import org.example.model.StarSystem;
import org.example.repository.CourseRepository;
import org.example.repository.GalaxyRepository;
import org.example.repository.StarSystemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GalaxyInformationService {
    private final GalaxyRepository galaxyRepository;
    private final StarSystemRepository starSystemRepository;
    private final CourseRepository courseRepository;

    private final ExplorerService explorerService;
    private final KeeperService keeperService;

    @Cacheable(cacheNames = "galaxiesCache", key = "#galaxy.galaxyId")
    @Transactional(readOnly = true)
    public GalaxyInformationGetResponse getGalaxyInformation(Galaxy galaxy) {
        List<StarSystem> systems = starSystemRepository.findSystemsByGalaxyId(galaxy.getGalaxyId());
        Flux<CourseGetResponse> fluxCourses = Flux.fromIterable(systems)
                .flatMap(s -> Mono.fromCallable(
                                () -> courseRepository.getCourseById(s.getSystemId()))
                        .subscribeOn(Schedulers.boundedElastic())
                );
        List<CourseGetResponse> courses = Objects.requireNonNull(fluxCourses.collectList().block());
        List<ExplorerWithSystemsDTO> explorers = explorerService.getExplorersWithSystems(courses);
        List<KeeperWithSystemsDTO> keepers = keeperService.getKeepersWithSystems(courses);
        return new GalaxyInformationGetResponse(
                galaxy.getGalaxyId(),
                galaxy.getGalaxyName(),
                galaxy.getGalaxyDescription(),
                systems.size(),
                explorers.size(),
                explorers,
                keepers.size(),
                keepers
        );
    }

    @KafkaListener(topics = "galaxyCacheTopic", containerFactory = "galaxyCacheKafkaListenerContainerFactory")
    @CacheEvict(cacheNames = "galaxiesCache", key = "#result")
    public Integer clearGalaxyCache(ConsumerRecord<String, Integer> message) {
        return galaxyRepository.getGalaxyIdBySystemId(message.value());
    }
}
