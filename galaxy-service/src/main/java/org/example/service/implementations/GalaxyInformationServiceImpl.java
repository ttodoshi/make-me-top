package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.course.GetCourseDto;
import org.example.dto.galaxy.GetGalaxyInformationDto;
import org.example.dto.person.PersonWithSystemsDto;
import org.example.model.Galaxy;
import org.example.model.StarSystem;
import org.example.repository.GalaxyRepository;
import org.example.repository.StarSystemRepository;
import org.example.service.CourseService;
import org.example.service.ExplorerService;
import org.example.service.GalaxyInformationService;
import org.example.service.KeeperService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalaxyInformationServiceImpl implements GalaxyInformationService {
    private final GalaxyRepository galaxyRepository;
    private final StarSystemRepository starSystemRepository;

    private final CourseService courseService;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;

    @Override
    @Cacheable(cacheNames = "galaxiesCache", key = "#galaxy.galaxyId")
    @Transactional(readOnly = true)
    public GetGalaxyInformationDto getGalaxyInformation(Galaxy galaxy) {
        List<StarSystem> systems = starSystemRepository.findSystemsByGalaxyId(galaxy.getGalaxyId());
        List<GetCourseDto> courses = systems.stream()
                .map(
                        s -> courseService.getCourseById(s.getSystemId())
                ).collect(Collectors.toList());
        List<PersonWithSystemsDto> explorers = explorerService.getExplorersWithSystems(courses);
        List<PersonWithSystemsDto> keepers = keeperService.getKeepersWithSystems(courses);
        return new GetGalaxyInformationDto(
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
