package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.mapper.DependencyMapper;
import org.example.dto.starsystem.StarSystemCreateRequest;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;
import org.example.dto.starsystem.SystemDependencyModel;
import org.example.event.CourseCreateEvent;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.StarSystem;
import org.example.repository.DependencyRepository;
import org.example.repository.StarSystemRepository;
import org.example.validator.StarSystemValidator;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StarSystemService {
    private final StarSystemRepository starSystemRepository;
    private final DependencyRepository dependencyRepository;

    private final StarSystemValidator starSystemValidator;
    private final ModelMapper mapper;
    private final DependencyMapper dependencyMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public StarSystemWithDependenciesGetResponse getStarSystemByIdWithDependencies(Integer systemId) {
        starSystemValidator.validateGetSystemWithDependencies(systemId);
        StarSystemWithDependenciesGetResponse system = mapper.map(
                starSystemRepository.getReferenceById(systemId), StarSystemWithDependenciesGetResponse.class);
        List<SystemDependencyModel> dependencies = new LinkedList<>();
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
        starSystemValidator.validateGetSystemsByGalaxyId(galaxyId);
        return starSystemRepository.findSystemsByGalaxyId(galaxyId);
    }

    @Transactional
    public StarSystem createSystem(Integer orbitId, StarSystemCreateRequest systemRequest) {
        starSystemValidator.validatePostRequest(orbitId, systemRequest);
        StarSystem system = mapper.map(systemRequest, StarSystem.class);
        system.setOrbitId(orbitId);
        StarSystem savedSystem = starSystemRepository.save(system);
        createCourse(savedSystem.getSystemId(), systemRequest);
        return savedSystem;
    }

    public void createCourse(Integer courseId, StarSystemCreateRequest starSystem) {
        kafkaTemplate.send("courseTopic", new CourseCreateEvent(courseId, starSystem.getSystemName(), starSystem.getDescription()));
    }

    public StarSystem updateSystem(Integer systemId, StarSystemDTO starSystem) {
        starSystemValidator.validatePutRequest(systemId, starSystem);
        StarSystem updatedStarSystem = starSystemRepository.getReferenceById(systemId);
        updatedStarSystem.setSystemName(starSystem.getSystemName());
        updatedStarSystem.setSystemPosition(starSystem.getSystemPosition());
        updatedStarSystem.setOrbitId(starSystem.getOrbitId());
        updatedStarSystem.setSystemLevel(starSystem.getSystemLevel());
        return starSystemRepository.save(updatedStarSystem);
    }

    public Map<String, String> deleteSystem(Integer systemId) {
        starSystemValidator.validateDeleteRequest(systemId);
        starSystemRepository.deleteById(systemId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Система " + systemId + " была уничтожена чёрной дырой");
        return response;
    }
}
