package org.example.galaxy.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.dto.starsystem.*;
import org.example.galaxy.exception.classes.system.SystemNotFoundException;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.repository.StarSystemRepository;
import org.example.galaxy.repository.SystemDependencyRepository;
import org.example.galaxy.service.validator.StarSystemValidatorService;
import org.example.galaxy.utils.mapper.DependencyMapper;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StarSystemService {
    private final StarSystemRepository starSystemRepository;
    private final SystemDependencyRepository systemDependencyRepository;

    private final StarSystemValidatorService starSystemValidatorService;
    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public GetStarSystemWithDependenciesDto findStarSystemByIdWithDependencies(Long systemId) {
        GetStarSystemWithDependenciesDto system = mapper.map(
                findStarSystemById(systemId),
                GetStarSystemWithDependenciesDto.class
        );

        List<SystemDependencyModelDto> dependencies = new ArrayList<>();
        systemDependencyRepository.getSystemChildren(systemId)
                .forEach(d -> dependencies.add(
                        DependencyMapper.dependencyToDependencyChildModel(d)
                ));
        systemDependencyRepository.getSystemParents(systemId)
                .stream()
                .filter(d -> d.getParent() != null)
                .forEach(d -> dependencies.add(
                        DependencyMapper.dependencyToDependencyParentModel(d)
                ));
        system.setSystemDependencyList(dependencies);

        return system;
    }

    @Transactional(readOnly = true)
    public StarSystemDto findStarSystemById(Long systemId) {
        return starSystemRepository.findById(systemId)
                .map(s -> mapper.map(s, StarSystemDto.class))
                .orElseThrow(() -> new SystemNotFoundException(systemId));
    }

    @Transactional(readOnly = true)
    public List<StarSystemDto> findStarSystemsByGalaxyId(Long galaxyId) {
        starSystemValidatorService.validateGetSystemsByGalaxyId(galaxyId);
        return starSystemRepository
                .findStarSystemsByOrbit_GalaxyId(galaxyId)
                .stream()
                .map(s -> mapper.map(s, StarSystemDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public StarSystemDto createSystem(Long orbitId, CreateStarSystemDto systemRequest) {
        starSystemValidatorService.validatePostRequest(orbitId, systemRequest);

        StarSystem system = mapper.map(systemRequest, StarSystem.class);
        system.setOrbitId(orbitId);

        return mapper.map(
                starSystemRepository.save(system),
                StarSystemDto.class
        );
    }

    @Transactional
    public StarSystemDto updateSystem(Long systemId, UpdateStarSystemDto starSystem) {
        StarSystem updatedStarSystem = starSystemRepository
                .findById(systemId)
                .orElseThrow(() -> new SystemNotFoundException(systemId));

        starSystemValidatorService.validatePutRequest(systemId, starSystem);

        updatedStarSystem.setSystemName(starSystem.getSystemName());
        updatedStarSystem.setSystemPosition(starSystem.getSystemPosition());
        updatedStarSystem.setOrbitId(starSystem.getOrbitId());
        updatedStarSystem.setSystemLevel(starSystem.getSystemLevel());

        return mapper.map(
                starSystemRepository.save(updatedStarSystem),
                StarSystemDto.class
        );
    }

    @KafkaListener(topics = "updateSystemTopic", containerFactory = "updateSystemKafkaListenerContainerFactory")
    @Transactional
    public void updateSystemName(ConsumerRecord<Long, String> record) {
        StarSystem starSystem = starSystemRepository.findById(record.key())
                .orElseThrow(() -> new SystemNotFoundException(record.key()));

        starSystem.setSystemName(record.value());
        starSystemRepository.save(starSystem);
    }

    @Transactional
    public MessageDto deleteSystem(Long systemId) {
        starSystemRepository.deleteById(systemId);
        return new MessageDto("Система " + systemId + " была уничтожена чёрной дырой");
    }
}
