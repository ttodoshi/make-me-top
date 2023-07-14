package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.config.mapper.DependencyMapper;
import org.example.dto.course.CourseDTO;
import org.example.dto.starsystem.*;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StarSystemService {
    private final StarSystemRepository starSystemRepository;
    private final DependencyRepository dependencyRepository;
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;

    private final ModelMapper mapper;
    private final DependencyMapper dependencyMapper;
    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger(GalaxyService.class.getName());
    @Setter
    private String token;
    @Value("${course_app_url}")
    private String COURSE_APP_URL;

    public GetStarSystemWithDependencies getStarSystemByIdWithDependencies(Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException();
        GetStarSystemWithDependencies system = mapper.map(
                starSystemRepository.getReferenceById(systemId), GetStarSystemWithDependencies.class);
        system.setDependencyList(dependencyRepository.getListSystemDependencyParent(systemId)
                .stream()
                .map(dependencyMapper::dependencyToDependencyParentModel)
                .collect(Collectors.toList()));
        List<SystemDependencyModel> dependencies = dependencyRepository.getListSystemDependencyChild(systemId)
                .stream()
                .filter(x -> x.getParent() != null)
                .map(dependencyMapper::dependencyToDependencyChildModel)
                .collect(Collectors.toList());
        for (SystemDependencyModel dependency : dependencies) {
            system.getDependencyList().add(dependency);
        }
        return system;
    }

    public GetStarSystem getStarSystemById(Integer systemId) {
        StarSystem system = starSystemRepository
                .findById(systemId).orElseThrow(SystemNotFoundException::new);
        return mapper.map(system, GetStarSystem.class);
    }

    public List<StarSystem> getStarSystemsByGalaxyId(Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException();
        return starSystemRepository.getStarSystemsByGalaxyId(galaxyId);
    }

    @Transactional
    public StarSystem createSystem(CreateStarSystem starSystem, Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException();
        if (!orbitRepository.existsById(starSystem.getOrbitId()))
            throw new OrbitNotFoundException();

        if (starSystemRepository.getStarSystemsByGalaxyId(galaxyId).stream()
                .noneMatch(
                        x -> Objects.equals(x.getSystemName(), starSystem.getSystemName()))) {
            StarSystem createdSystem = starSystemRepository.save(mapper.map(starSystem, StarSystem.class));
            createCourse(createdSystem.getSystemId(), starSystem);
            return createdSystem;
        } else
            throw new SystemAlreadyExistsException();
    }

    private void createCourse(Integer courseId, CreateStarSystem starSystem) {
        HttpEntity<CourseDTO> entity = new HttpEntity<>(new CourseDTO(courseId,
                starSystem.getSystemName(), new Date(), new Date(), starSystem.getDescription()),
                createHeaders());
        restTemplate.postForEntity(COURSE_APP_URL + "/course/", entity, CourseDTO.class);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }

    @Transactional
    public StarSystem updateSystem(StarSystemDTO starSystem, Integer galaxyId, Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException();
        StarSystem updatedStarSystem = starSystemRepository.getReferenceById(systemId);
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException();
        if (!orbitRepository.existsById(starSystem.getOrbitId()))
            throw new OrbitNotFoundException();
        if (starSystemRepository.getStarSystemsByGalaxyId(galaxyId).stream()
                .noneMatch(
                        x -> Objects.equals(x.getSystemName(), starSystem.getSystemName()))) {
            updatedStarSystem.setSystemName(starSystem.getSystemName());
            updatedStarSystem.setSystemPosition(starSystem.getSystemPosition());
            updatedStarSystem.setOrbitId(starSystem.getOrbitId());
            updatedStarSystem.setSystemLevel(starSystem.getSystemLevel());
            return starSystemRepository.save(updatedStarSystem);
        } else {
            throw new SystemAlreadyExistsException();
        }
    }

    @Transactional
    public Map<String, String> deleteSystem(Integer systemId) {
        try {
            starSystemRepository.deleteById(systemId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Система " + systemId + " была уничтожена чёрной дырой");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemNotFoundException();
        }
    }
}
