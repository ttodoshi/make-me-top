package org.example.galaxy.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.galaxy.config.mapper.DependencyMapper;
import org.example.course.dto.event.CourseCreateEvent;
import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.dto.starsystem.CreateStarSystemDto;
import org.example.galaxy.dto.starsystem.GetStarSystemWithDependenciesDto;
import org.example.galaxy.dto.starsystem.StarSystemDto;
import org.example.galaxy.dto.starsystem.SystemDependencyModelDto;
import org.example.galaxy.exception.classes.system.SystemNotFoundException;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.repository.StarSystemRepository;
import org.example.galaxy.repository.SystemDependencyRepository;
import org.example.galaxy.service.validator.StarSystemValidatorService;
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
    private final SystemDependencyRepository systemDependencyRepository;

    private final StarSystemValidatorService starSystemValidatorService;
    private final ModelMapper mapper;
    private final DependencyMapper dependencyMapper;

    private final KafkaTemplate<String, Object> createCourseKafkaTemplate;
    private final KafkaTemplate<Integer, String> updateCourseKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteCourseKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deletePlanetsKafkaTemplate;

    public StarSystemService(StarSystemRepository starSystemRepository, SystemDependencyRepository systemDependencyRepository,
                             StarSystemValidatorService starSystemValidatorService, ModelMapper mapper, DependencyMapper dependencyMapper,
                             KafkaTemplate<String, Object> createCourseKafkaTemplate,
                             KafkaTemplate<Integer, String> updateCourseKafkaTemplate,
                             @Qualifier("deleteCourseKafkaTemplate") KafkaTemplate<Integer, Integer> deleteCourseKafkaTemplate,
                             @Qualifier("deletePlanetsKafkaTemplate") KafkaTemplate<Integer, Integer> deletePlanetsKafkaTemplate) {
        this.starSystemRepository = starSystemRepository;
        this.systemDependencyRepository = systemDependencyRepository;
        this.starSystemValidatorService = starSystemValidatorService;
        this.mapper = mapper;
        this.dependencyMapper = dependencyMapper;
        this.createCourseKafkaTemplate = createCourseKafkaTemplate;
        this.updateCourseKafkaTemplate = updateCourseKafkaTemplate;
        this.deleteCourseKafkaTemplate = deleteCourseKafkaTemplate;
        this.deletePlanetsKafkaTemplate = deletePlanetsKafkaTemplate;
    }

    @Transactional(readOnly = true)
    public GetStarSystemWithDependenciesDto findStarSystemByIdWithDependencies(Integer systemId) {
        GetStarSystemWithDependenciesDto system = mapper.map(
                findStarSystemById(systemId),
                GetStarSystemWithDependenciesDto.class
        );
        List<SystemDependencyModelDto> dependencies = new ArrayList<>();
        systemDependencyRepository.getSystemChildren(systemId)
                .stream()
                .map(dependencyMapper::dependencyToDependencyChildModel)
                .forEach(dependencies::add);
        systemDependencyRepository.getSystemParents(systemId)
                .stream()
                .filter(d -> d.getParent() != null)
                .map(dependencyMapper::dependencyToDependencyParentModel)
                .forEach(dependencies::add);
        system.setDependencyList(dependencies);
        return system;
    }

    @Transactional(readOnly = true)
    public StarSystem findStarSystemById(Integer systemId) {
        return starSystemRepository.findById(systemId)
                .orElseThrow(() -> new SystemNotFoundException(systemId));
    }

    @Transactional(readOnly = true)
    public List<StarSystem> findStarSystemsByGalaxyId(Integer galaxyId) {
        starSystemValidatorService.validateGetSystemsByGalaxyId(galaxyId);
        return starSystemRepository.findStarSystemsByOrbit_GalaxyId(galaxyId);
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
        createCourseKafkaTemplate.send(
                "createCourseTopic",
                new CourseCreateEvent(
                        courseId,
                        starSystem.getSystemName(),
                        starSystem.getDescription()
                )
        );
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

    public void deletePlanetsBySystemId(Integer systemId) {
        deletePlanetsKafkaTemplate.send("deletePlanetsTopic", systemId);
    }

    public void deleteCourse(Integer systemId) {
        deleteCourseKafkaTemplate.send("deleteCourseTopic", systemId);
    }
}
