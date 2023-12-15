package org.example.galaxy.service;

import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.dto.orbit.CreateOrbitWithStarSystemsDto;
import org.example.galaxy.dto.orbit.GetOrbitWithStarSystemsDto;
import org.example.galaxy.dto.orbit.OrbitDto;
import org.example.galaxy.dto.orbit.UpdateOrbitDto;
import org.example.galaxy.exception.classes.orbit.OrbitNotFoundException;
import org.example.galaxy.model.Orbit;
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
        GetOrbitWithStarSystemsDto orbitWithStarSystems = mapper.map(
                findOrbitById(orbitId),
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
    public OrbitDto findOrbitById(Long orbitId) {
        return orbitRepository.findById(orbitId)
                .map(o -> mapper.map(o, OrbitDto.class))
                .orElseThrow(() -> new OrbitNotFoundException(orbitId));
    }

    @Transactional
    public Long createOrbit(Long galaxyId, CreateOrbitWithStarSystemsDto orbitRequest) {
        orbitValidatorService.validatePostRequest(galaxyId, orbitRequest);

        Orbit orbit = mapper.map(orbitRequest, Orbit.class);
        orbit.setGalaxyId(galaxyId);
        Long savedOrbitId = orbitRepository.save(orbit).getOrbitId();

        orbitRequest.getSystemList().forEach(s ->
                systemService.createSystem(savedOrbitId, s));

        return savedOrbitId;
    }

    @Transactional
    public OrbitDto updateOrbit(Long orbitId, UpdateOrbitDto orbit) {
        orbitValidatorService.validatePutRequest(orbitId, orbit);

        Orbit updatedOrbit = orbitRepository.findById(orbitId)
                .orElseThrow(() -> new OrbitNotFoundException(orbitId));
        updatedOrbit.setOrbitLevel(orbit.getOrbitLevel());
        updatedOrbit.setSystemCount(orbit.getSystemCount());
        updatedOrbit.setGalaxyId(orbit.getGalaxyId());

        return mapper.map(
                orbitRepository.save(updatedOrbit),
                OrbitDto.class
        );
    }

    @Transactional
    public MessageDto deleteOrbit(Long orbitId) {
        orbitRepository.deleteById(orbitId);
        return new MessageDto("Орбита " + orbitId + " была уничтожена неизвестным оружием инопланетной цивилизации");
    }
}
