package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.config.mapper.DependencyMapper;
import org.example.dto.course.CourseDTO;
import org.example.dto.starsystem.*;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.StarSystem;
import org.example.repository.DependencyRepository;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StarSystemService {
    private final StarSystemRepository starSystemRepository;
    private final DependencyRepository dependencyRepository;
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;

    private final ModelMapper mapper;
    private final DependencyMapper dependencyMapper;

    @Setter
    private String token;
    @Value("${course_app_url}")
    private String COURSE_APP_URL;

    public StarSystemWithDependenciesGetResponse getStarSystemByIdWithDependencies(Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
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

    public StarSystemGetResponse getStarSystemById(Integer systemId) {
        StarSystem system = starSystemRepository
                .findById(systemId).orElseThrow(() -> new SystemNotFoundException(systemId));
        return mapper.map(system, StarSystemGetResponse.class);
    }

    public List<StarSystem> getStarSystemsByGalaxyId(Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
        return starSystemRepository.findSystemsByGalaxyId(galaxyId);
    }

    @Transactional
    public StarSystem createSystem(Integer orbitId, StarSystemCreateRequest systemRequest) {
        if (!orbitRepository.existsById(orbitId))
            throw new OrbitNotFoundException(orbitId);
        if (systemExists(orbitRepository.getReferenceById(orbitId).getGalaxyId(), systemRequest.getSystemName()))
            throw new SystemAlreadyExistsException(systemRequest.getSystemName());
        StarSystem system = mapper.map(systemRequest, StarSystem.class);
        system.setOrbitId(orbitId);
        StarSystem savedSystem = starSystemRepository.save(system);
        createCourse(savedSystem.getSystemId(), systemRequest);
        return savedSystem;
    }

    public void createCourse(Integer courseId, StarSystemCreateRequest starSystem) {
        WebClient webClient = WebClient.create(COURSE_APP_URL);
        webClient.post()
                .uri("/course/")
                .bodyValue(new CourseDTO(courseId, starSystem.getSystemName(), new Date(), new Date(), starSystem.getDescription()))
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(Void.class)
                .subscribe();
    }

    public StarSystem updateSystem(StarSystemDTO starSystem, Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
        StarSystem updatedStarSystem = starSystemRepository.getReferenceById(systemId);
        if (!orbitRepository.existsById(starSystem.getOrbitId()))
            throw new OrbitNotFoundException(starSystem.getOrbitId());
        if (systemExists(orbitRepository.getReferenceById(starSystem.getOrbitId()).getGalaxyId(), starSystem.getSystemName()))
            throw new SystemAlreadyExistsException(starSystem.getSystemName());
        updatedStarSystem.setSystemName(starSystem.getSystemName());
        updatedStarSystem.setSystemPosition(starSystem.getSystemPosition());
        updatedStarSystem.setOrbitId(starSystem.getOrbitId());
        updatedStarSystem.setSystemLevel(starSystem.getSystemLevel());
        return starSystemRepository.save(updatedStarSystem);
    }

    private boolean systemExists(Integer galaxyId, String systemName) {
        return starSystemRepository.findSystemsByGalaxyId(galaxyId)
                .stream().anyMatch(s -> Objects.equals(s.getSystemName(), systemName));
    }

    public Map<String, String> deleteSystem(Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
        starSystemRepository.deleteById(systemId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Система " + systemId + " была уничтожена чёрной дырой");
        return response;
    }
}
