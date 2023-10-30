package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.message.MessageDto;
import org.example.dto.orbit.CreateOrbitWithStarSystemsDto;
import org.example.dto.orbit.GetOrbitWithStarSystemsDto;
import org.example.dto.orbit.OrbitDto;
import org.example.dto.starsystem.GetStarSystemWithDependenciesDto;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.model.Orbit;
import org.example.model.StarSystem;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.example.service.validator.OrbitValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrbitService {
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final OrbitValidatorService orbitValidatorService;
    private final StarSystemService systemService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public GetOrbitWithStarSystemsDto getOrbitWithSystemList(Integer orbitId) {
        orbitValidatorService.validateGetWithSystemListRequest(orbitId);
        GetOrbitWithStarSystemsDto orbit = mapper.map(
                orbitRepository.getReferenceById(orbitId), GetOrbitWithStarSystemsDto.class);
        List<GetStarSystemWithDependenciesDto> systemWithDependenciesList = new ArrayList<>();
        starSystemRepository.findStarSystemsByOrbitId(orbitId).forEach(
                s -> systemWithDependenciesList.add(
                        systemService.getStarSystemByIdWithDependencies(s.getSystemId())
                )
        );
        orbit.setSystemWithDependenciesList(systemWithDependenciesList);
        return orbit;
    }

    @Transactional(readOnly = true)
    public Orbit getOrbitById(Integer orbitId) {
        return orbitRepository.findById(orbitId)
                .orElseThrow(() -> new OrbitNotFoundException(orbitId));
    }

    @Transactional
    public GetOrbitWithStarSystemsDto createOrbit(Integer galaxyId, CreateOrbitWithStarSystemsDto orbitRequest) {
        orbitValidatorService.validatePostRequest(galaxyId, orbitRequest);
        Orbit orbit = mapper.map(orbitRequest, Orbit.class);
        orbit.setGalaxyId(galaxyId);
        Orbit savedOrbit = orbitRepository.save(orbit);
        orbitRequest.getSystemList().forEach(s -> {
                    StarSystem system = mapper.map(s, StarSystem.class);
                    system.setOrbitId(savedOrbit.getOrbitId());
                    StarSystem savedSystem = starSystemRepository.save(system);
                    systemService.createCourse(savedSystem.getSystemId(), s);
                }
        );
        return getOrbitWithSystemList(savedOrbit.getOrbitId());
    }

    @Transactional
    public Orbit updateOrbit(Integer orbitId, OrbitDto orbit) {
        orbitValidatorService.validatePutRequest(orbitId, orbit);
        Orbit updatedOrbit = orbitRepository.findById(orbitId).orElseThrow(() -> new OrbitNotFoundException(orbitId));
        updatedOrbit.setOrbitLevel(orbit.getOrbitLevel());
        updatedOrbit.setSystemCount(orbit.getSystemCount());
        updatedOrbit.setGalaxyId(orbit.getGalaxyId());
        return orbitRepository.save(updatedOrbit);
    }

    @Transactional
    public MessageDto deleteOrbit(Integer orbitId) {
        Orbit orbit = orbitRepository.findById(orbitId)
                .orElseThrow(() -> new OrbitNotFoundException(orbitId));
        orbit.getSystems().forEach(s -> {
            clearCourseAndPlanets(s.getSystemId());
        });
        orbitRepository.deleteById(orbitId);
        return new MessageDto("Орбита " + orbitId + " была уничтожена неизвестным оружием инопланетной цивилизации");
    }

    public void clearCourseAndPlanets(Integer systemId) {
        systemService.deleteCourse(systemId);
        systemService.deletePlanetsBySystemId(systemId);
    }
}
