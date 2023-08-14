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
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GalaxyInformationService {
    private final GalaxyRepository galaxyRepository;
    private final StarSystemRepository starSystemRepository;
    private final CourseRepository courseRepository;

    private final ModelMapper mapper;

    @Cacheable(cacheNames = "galaxiesCache", key = "#galaxy.galaxyId")
    @Transactional(readOnly = true)
    public GalaxyInformationGetResponse getGalaxyInformation(Galaxy galaxy) {
        List<StarSystem> systems = starSystemRepository.findSystemsByGalaxyId(galaxy.getGalaxyId());
        Map<Integer, KeeperWithSystemsDTO> keepers = new HashMap<>();
        Map<Integer, ExplorerWithSystemsDTO> explorers = new HashMap<>();
        for (StarSystem system : systems) {
            CourseGetResponse course = courseRepository.getCourseById(system.getSystemId());
            course.getExplorers().forEach(
                    e -> {
                        if (explorers.containsKey(e.getPersonId())) {
                            explorers.get(e.getPersonId()).getSystems()
                                    .add(system.getSystemId());
                        } else {
                            ExplorerWithSystemsDTO explorerWithSystems = mapper.map(e, ExplorerWithSystemsDTO.class);
                            List<Integer> explorerSystems = new LinkedList<>();
                            explorerSystems.add(system.getSystemId());
                            explorers.put(e.getPersonId(), explorerWithSystems
                                    .withSystems(explorerSystems)
                            );
                        }
                    }
            );
            course.getKeepers().forEach(
                    k -> {
                        if (keepers.containsKey(k.getPersonId())) {
                            keepers.get(k.getPersonId()).getSystems()
                                    .add(system.getSystemId());
                        } else {
                            KeeperWithSystemsDTO keeperWithSystems = mapper.map(k, KeeperWithSystemsDTO.class);
                            List<Integer> keeperSystems = new LinkedList<>();
                            keeperSystems.add(system.getSystemId());
                            keepers.put(k.getPersonId(), keeperWithSystems
                                    .withSystems(keeperSystems)
                            );
                        }
                    }
            );
        }
        return new GalaxyInformationGetResponse(
                galaxy.getGalaxyId(),
                galaxy.getGalaxyName(),
                galaxy.getGalaxyDescription(),
                systems.size(),
                explorers.size(),
                explorers.values(),
                keepers.size(),
                keepers.values()
        );
    }

    @KafkaListener(topics = "galaxyCacheTopic", containerFactory = "galaxyCacheKafkaListenerContainerFactory")
    @CacheEvict(cacheNames = "galaxiesCache", key = "#result")
    public Integer clearGalaxyCache(ConsumerRecord<String, Integer> message) {
        return galaxyRepository.getGalaxyIdBySystemId(message.value());
    }
}
