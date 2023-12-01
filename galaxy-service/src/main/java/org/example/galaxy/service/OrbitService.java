package org.example.galaxy.service;

import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.dto.orbit.CreateOrbitWithStarSystemsDto;
import org.example.galaxy.dto.orbit.GetOrbitWithStarSystemsDto;
import org.example.galaxy.dto.orbit.OrbitDto;
import org.example.galaxy.exception.classes.orbit.OrbitNotFoundException;
import org.example.galaxy.model.Orbit;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.repository.OrbitRepository;
import org.example.galaxy.repository.StarSystemRepository;
import org.example.galaxy.service.validator.OrbitValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrbitService {
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final OrbitValidatorService orbitValidatorService;
    private final StarSystemService systemService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public GetOrbitWithStarSystemsDto findOrbitWithSystemList(Long orbitId) {
        Orbit orbit = findOrbitById(orbitId);

        GetOrbitWithStarSystemsDto orbitWithStarSystems = mapper.map(
                orbit,
                GetOrbitWithStarSystemsDto.class
        );

        orbitWithStarSystems.setSystemWithDependenciesList(
                starSystemRepository.findStarSystemsByOrbitId(orbitId)
                        .stream()
                        .map(s -> systemService.findStarSystemByIdWithDependencies(s.getSystemId()))
                        .collect(Collectors.toList())
        );
        return orbitWithStarSystems;
    }

    @Transactional(readOnly = true)
    public Orbit findOrbitById(Long orbitId) {
        return orbitRepository.findById(orbitId)
                .orElseThrow(() -> new OrbitNotFoundException(orbitId));
    }

    @Transactional
    public GetOrbitWithStarSystemsDto createOrbit(Long galaxyId, CreateOrbitWithStarSystemsDto createOrbitRequest) {
        orbitValidatorService.validatePostRequest(galaxyId, createOrbitRequest);

        Orbit orbit = mapper.map(createOrbitRequest, Orbit.class);
        orbit.setGalaxyId(galaxyId);
        Orbit savedOrbit = orbitRepository.save(orbit);

        createOrbitRequest.getSystemList().forEach(s -> {
            StarSystem system = mapper.map(s, StarSystem.class);
            system.setOrbitId(savedOrbit.getOrbitId());
            StarSystem savedSystem = starSystemRepository.save(system);
            systemService.createCourse(savedSystem.getSystemId(), s);
        });
        return findOrbitWithSystemList(savedOrbit.getOrbitId());
    }

    @Transactional
    public Orbit updateOrbit(Long orbitId, OrbitDto orbit) {
        orbitValidatorService.validatePutRequest(orbitId, orbit);
        Orbit updatedOrbit = findOrbitById(orbitId);
        updatedOrbit.setOrbitLevel(orbit.getOrbitLevel());
        updatedOrbit.setSystemCount(orbit.getSystemCount());
        updatedOrbit.setGalaxyId(orbit.getGalaxyId());
        return orbitRepository.save(updatedOrbit);
    }

    @Transactional
    public MessageDto deleteOrbit(Long orbitId) {
        findOrbitById(orbitId)
                .getSystems()
                .forEach(s -> systemService
                        .clearCourseAndPlanets(s.getSystemId())
                );
        orbitRepository.deleteById(orbitId);
        return new MessageDto("Орбита " + orbitId + " была уничтожена неизвестным оружием инопланетной цивилизации");
    }
}
