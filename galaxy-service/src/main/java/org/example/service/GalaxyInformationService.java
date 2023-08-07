package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.course.CourseGetResponse;
import org.example.dto.explorer.ExplorerDTO;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.dto.keeper.KeeperDTO;
import org.example.model.Galaxy;
import org.example.model.StarSystem;
import org.example.repository.GalaxyRepository;
import org.example.repository.StarSystemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GalaxyInformationService {
    private final GalaxyRepository galaxyRepository;
    private final StarSystemRepository starSystemRepository;

    @Setter
    private String token;

    @Cacheable(cacheNames = "galaxiesCache", key = "#galaxy.galaxyId")
    public GalaxyInformationGetResponse getGalaxyInformation(Galaxy galaxy) {
        List<StarSystem> systems = starSystemRepository.findSystemsByGalaxyId(galaxy.getGalaxyId());
        Map<Integer, KeeperDTO> keepers = new HashMap<>();
        Map<Integer, ExplorerDTO> explorers = new HashMap<>();
        for (StarSystem system : systems) {
            CourseGetResponse course = getCourseById(system.getSystemId());
            course.getExplorers().forEach(
                    e -> explorers.putIfAbsent(e.getPersonId(), e)
            );
            course.getKeepers().forEach(
                    k -> keepers.putIfAbsent(k.getPersonId(), k)
            );
        }
        return new GalaxyInformationGetResponse(
                galaxy.getGalaxyId(),
                galaxy.getGalaxyName(),
                galaxy.getGalaxyDescription(),
                systems.size(),
                explorers.size(),
                keepers.size(),
                keepers.values()
        );
    }

    private CourseGetResponse getCourseById(Integer courseId) {
        WebClient webClient = WebClient.create("http://10.10.0.9:8106/course-app/");
        return webClient.get()
                .uri("course/" + courseId + "/")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(CourseGetResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    @KafkaListener(topics = "galaxyCacheTopic", containerFactory = "galaxyCacheKafkaListenerContainerFactory")
    @CacheEvict(cacheNames = "galaxiesCache", key = "#result")
    public Integer clearGalaxyCache(ConsumerRecord<String, Integer> message) {
        return galaxyRepository.getGalaxyIdBySystemId(message.value());
    }
}
