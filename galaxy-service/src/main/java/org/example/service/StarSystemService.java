package org.example.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.config.mapper.DependencyMapper;
import org.example.dto.event.CourseCreateEvent;
import org.example.dto.message.MessageDto;
import org.example.dto.starsystem.CreateStarSystemDto;
import org.example.dto.starsystem.GetStarSystemWithDependenciesDto;
import org.example.dto.starsystem.StarSystemDto;
import org.example.dto.starsystem.SystemDependencyModelDto;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.StarSystem;
import org.example.repository.DependencyRepository;
import org.example.repository.StarSystemRepository;
import org.example.service.validator.StarSystemValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StarSystemService {
    private final StarSystemRepository starSystemRepository;
    private final DependencyRepository dependencyRepository;

    private final StarSystemValidatorService starSystemValidatorService;
    private final ModelMapper mapper;
    private final DependencyMapper dependencyMapper;

    private final KafkaTemplate<String, Object> createCourseKafkaTemplate;
    private final KafkaTemplate<Integer, String> updateCourseKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteCourseKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deletePlanetsKafkaTemplate;

    public StarSystemService(StarSystemRepository starSystemRepository, DependencyRepository dependencyRepository, StarSystemValidatorService starSystemValidatorService, ModelMapper mapper, DependencyMapper dependencyMapper, KafkaTemplate<String, Object> createCourseKafkaTemplate, KafkaTemplate<Integer, String> updateCourseKafkaTemplate, @Qualifier("deleteCourseKafkaTemplate") KafkaTemplate<Integer, Integer> deleteCourseKafkaTemplate, @Qualifier("deletePlanetsKafkaTemplate") KafkaTemplate<Integer, Integer> deletePlanetsKafkaTemplate) {
        this.starSystemRepository = starSystemRepository;
        this.dependencyRepository = dependencyRepository;
        this.starSystemValidatorService = starSystemValidatorService;
        this.mapper = mapper;
        this.dependencyMapper = dependencyMapper;
        this.createCourseKafkaTemplate = createCourseKafkaTemplate;
        this.updateCourseKafkaTemplate = updateCourseKafkaTemplate;
        this.deleteCourseKafkaTemplate = deleteCourseKafkaTemplate;
        this.deletePlanetsKafkaTemplate = deletePlanetsKafkaTemplate;
    }

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

    @Transactional(readOnly = true)
    public StarSystem getStarSystemById(Integer systemId) {
        return starSystemRepository
                .findById(systemId).orElseThrow(() -> new SystemNotFoundException(systemId));
    }

    @Transactional(readOnly = true)
    public List<StarSystem> getStarSystemsByGalaxyId(Integer galaxyId) {
        starSystemValidatorService.validateGetSystemsByGalaxyId(galaxyId);
        return starSystemRepository.findSystemsByGalaxyId(galaxyId);
    }

    @Transactional
    public StarSystem createSystem(Integer orbitId, CreateStarSystemDto systemRequest) {
        starSystemValidatorService.validatePostRequest(orbitId, systemRequest);
        StarSystem system = mapper.map(systemRequest, StarSystem.class);
        system.setOrbitId(orbitId);
        StarSystem savedSystem = starSystemRepository.save(system);
        createCourse(savedSystem.getSystemId(), systemRequest);
        return savedSystem;
    }

    public void createCourse(Integer courseId, CreateStarSystemDto starSystem) {
        createCourseKafkaTemplate.send("createCourseTopic", new CourseCreateEvent(courseId, starSystem.getSystemName(), starSystem.getDescription()));
    }

    @Transactional
    public StarSystem updateSystem(Integer systemId, StarSystemDto starSystem) {
        starSystemValidatorService.validatePutRequest(systemId, starSystem);
        StarSystem updatedStarSystem = starSystemRepository.getReferenceById(systemId);
        updatedStarSystem.setSystemName(starSystem.getSystemName());
        updatedStarSystem.setSystemPosition(starSystem.getSystemPosition());
        updatedStarSystem.setOrbitId(starSystem.getOrbitId());
        updatedStarSystem.setSystemLevel(starSystem.getSystemLevel());
        updateCourseTitle(systemId, starSystem.getSystemName());
        return starSystemRepository.save(updatedStarSystem);
    }

    @KafkaListener(topics = "updateSystemTopic", containerFactory = "updateSystemKafkaListenerContainerFactory")
    @Transactional
    public void updateSystemName(ConsumerRecord<Integer, String> record) {
        StarSystem starSystem = starSystemRepository.findById(record.key())
                .orElseThrow(() -> new SystemNotFoundException(record.key()));
        starSystem.setSystemName(record.value());
        starSystemRepository.save(starSystem);
    }

    private void updateCourseTitle(Integer systemId, String systemName) {
        updateCourseKafkaTemplate.send("updateCourseTopic", systemId, systemName);
    }

    @Transactional
    public MessageDto deleteSystem(Integer systemId) {
        starSystemValidatorService.validateDeleteRequest(systemId);
        starSystemRepository.deleteById(systemId);
        deletePlanetsBySystemId(systemId);
        deleteCourse(systemId);
        return new MessageDto("Система " + systemId + " была уничтожена чёрной дырой");
    }

    private void deletePlanetsBySystemId(Integer systemId) {
        deletePlanetsKafkaTemplate.send("deletePlanetsTopic", systemId);
    }

    private void deleteCourse(Integer systemId) {
        deleteCourseKafkaTemplate.send("deleteCourseTopic", systemId);
    }
}
