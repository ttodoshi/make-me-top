package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.CreateGalaxyDto;
import org.example.dto.galaxy.GalaxyDto;
import org.example.dto.galaxy.GetGalaxyDto;
import org.example.dto.galaxy.GetGalaxyInformationDto;
import org.example.dto.orbit.GetOrbitWithStarSystemsWithoutGalaxyIdDto;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.Galaxy;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.example.service.validator.GalaxyValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalaxyService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final GalaxyValidatorService galaxyValidatorService;
    private final OrbitService orbitService;
    private final GalaxyInformationService galaxyInformationService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<GetGalaxyInformationDto> getAllGalaxies() {
        return galaxyRepository.findAll()
                .stream()
                .map(galaxyInformationService::getGalaxyInformation)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GetGalaxyDto getGalaxyById(Integer galaxyId) {
        galaxyValidatorService.validateGetByIdRequest(galaxyId);
        GetGalaxyDto galaxy = mapper.map(galaxyRepository.getReferenceById(galaxyId), GetGalaxyDto.class);
        List<GetOrbitWithStarSystemsWithoutGalaxyIdDto> orbitWithStarSystemsList = new ArrayList<>();
        orbitRepository.findOrbitsByGalaxyId(galaxyId).forEach(
                o -> orbitWithStarSystemsList.add(
                        mapper.map(
                                orbitService.getOrbitWithSystemList(o.getOrbitId()),
                                GetOrbitWithStarSystemsWithoutGalaxyIdDto.class)
                )
        );
        galaxy.setOrbitList(orbitWithStarSystemsList);
        return galaxy;
    }

    public Galaxy getGalaxyBySystemId(Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
        Integer galaxyId = galaxyRepository.getGalaxyIdBySystemId(systemId);
        return galaxyRepository.findById(galaxyId)
                .orElseThrow(() -> new GalaxyNotFoundException(galaxyId));
    }

    @Transactional
    public GetGalaxyDto createGalaxy(CreateGalaxyDto createGalaxyDto) {
        galaxyValidatorService.validatePostRequest(createGalaxyDto);
        Galaxy galaxy = mapper.map(createGalaxyDto, Galaxy.class);
        Integer savedGalaxyId = galaxyRepository.save(galaxy).getGalaxyId();
        createGalaxyDto.getOrbitList().forEach(o -> orbitService.createOrbit(savedGalaxyId, o));
        return getGalaxyById(savedGalaxyId);
    }

    @CacheEvict(cacheNames = "galaxiesCache", key = "#galaxyId")
    public Galaxy updateGalaxy(Integer galaxyId, GalaxyDto galaxy) {
        galaxyValidatorService.validatePutRequest(galaxyId, galaxy);
        Galaxy updatedGalaxy = galaxyRepository.getReferenceById(galaxyId);
        updatedGalaxy.setGalaxyName(galaxy.getGalaxyName());
        updatedGalaxy.setGalaxyDescription(galaxy.getGalaxyDescription());
        return galaxyRepository.save(updatedGalaxy);
    }

    @CacheEvict(cacheNames = "galaxiesCache", key = "#galaxyId")
    public Map<String, String> deleteGalaxy(Integer galaxyId) {
        galaxyValidatorService.validateDeleteRequest(galaxyId);
        galaxyRepository.deleteById(galaxyId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Галактика " + galaxyId + " была уничтожена квазаром");
        return response;
    }
}
