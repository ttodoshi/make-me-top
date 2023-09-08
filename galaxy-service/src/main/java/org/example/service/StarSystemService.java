package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.mapper.DependencyMapper;
import org.example.dto.event.CourseCreateEvent;
import org.example.dto.starsystem.CreateStarSystemDto;
import org.example.dto.starsystem.StarSystemDto;
import org.example.dto.starsystem.GetStarSystemWithDependenciesDto;
import org.example.dto.starsystem.SystemDependencyModelDto;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.StarSystem;
import org.example.repository.DependencyRepository;
import org.example.repository.StarSystemRepository;
import org.example.service.validator.StarSystemValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StarSystemService {
    private final StarSystemRepository starSystemRepository;
    private final DependencyRepository dependencyRepository;

    private final StarSystemValidatorService starSystemValidatorService;
    private final ModelMapper mapper;
    private final DependencyMapper dependencyMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional(readOnly = true)
    public GetStarSystemWithDependenciesDto getStarSystemByIdWithDependencies(Integer systemId) {
        starSystemValidatorService.validateGetSystemWithDependencies(systemId);
        GetStarSystemWithDependenciesDto system = mapper.map(
                starSystemRepository.getReferenceById(systemId), GetStarSystemWithDependenciesDto.class);
        List<SystemDependencyModelDto> dependencies = new ArrayList<>();
        dependencyRepository.getSystemChildren(systemId)
                .stream()
                .map(dependencyMapper::dependencyToDependencyChildModel)
                .forEach(dependencies::add);
        dependencyRepository.getSystemParents(systemId)
                .forEach(s -> {
                    if (s.getParent() != null)
                        dependencies.add(dependencyMapper.dependencyToDependencyParentModel(s));
                });
        system.setDependencyList(dependencies);
        return system;
    }

    public StarSystem getStarSystemById(Integer systemId) {
        return starSystemRepository
                .findById(systemId).orElseThrow(() -> new SystemNotFoundException(systemId));
    }

    public List<StarSystem> getStarSystemsByGalaxyId(Integer galaxyId) {
        starSystemValidatorService.validateGetSystemsByGalaxyId(galaxyId);
        return starSystemRepository.findSystemsByGalaxyId(galaxyId);
    }

    @Transactional
    @CacheEvict(cacheNames = "galaxiesCache", key = "@orbitService.getOrbitById(#orbitId).galaxyId")
    public StarSystem createSystem(Integer orbitId, CreateStarSystemDto systemRequest) {
        starSystemValidatorService.validatePostRequest(orbitId, systemRequest);
        StarSystem system = mapper.map(systemRequest, StarSystem.class);
        system.setOrbitId(orbitId);
        StarSystem savedSystem = starSystemRepository.save(system);
        createCourse(savedSystem.getSystemId(), systemRequest);
        return savedSystem;
    }

    public void createCourse(Integer courseId, CreateStarSystemDto starSystem) {
        kafkaTemplate.send("courseTopic", new CourseCreateEvent(courseId, starSystem.getSystemName(), starSystem.getDescription()));
    }

    public StarSystem updateSystem(Integer systemId, StarSystemDto starSystem) {
        starSystemValidatorService.validatePutRequest(systemId, starSystem);
        StarSystem updatedStarSystem = starSystemRepository.getReferenceById(systemId);
        updatedStarSystem.setSystemName(starSystem.getSystemName());
        updatedStarSystem.setSystemPosition(starSystem.getSystemPosition());
        updatedStarSystem.setOrbitId(starSystem.getOrbitId());
        updatedStarSystem.setSystemLevel(starSystem.getSystemLevel());
        return starSystemRepository.save(updatedStarSystem);
    }

    @CacheEvict(cacheNames = "galaxiesCache", key = "@orbitService.getOrbitById(@starSystemService.getStarSystemById(#systemId).orbitId).galaxyId", beforeInvocation = true)
    public Map<String, String> deleteSystem(Integer systemId) {
        starSystemValidatorService.validateDeleteRequest(systemId);
        starSystemRepository.deleteById(systemId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Система " + systemId + " была уничтожена чёрной дырой");
        return response;
    }
}
