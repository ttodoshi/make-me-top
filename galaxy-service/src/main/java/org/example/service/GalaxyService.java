package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.GalaxyCreateRequest;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.galaxy.GalaxyGetResponse;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.dto.orbit.OrbitWithStarSystemsCreateRequest;
import org.example.dto.orbit.OrbitWithStarSystemsWithoutGalaxyIdGetResponse;
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public List<GalaxyInformationGetResponse> getAllGalaxies() {
        List<GalaxyInformationGetResponse> galaxies = new LinkedList<>();
        for (Galaxy galaxy : galaxyRepository.findAll()) {
            galaxies.add(galaxyInformationService.getGalaxyInformation(galaxy));
        }
        return galaxies;
    }

    @Transactional(readOnly = true)
    public GalaxyGetResponse getGalaxyById(Integer galaxyId) {
        galaxyValidatorService.validateGetByIdRequest(galaxyId);
        GalaxyGetResponse galaxy = mapper.map(galaxyRepository.getReferenceById(galaxyId), GalaxyGetResponse.class);
        List<OrbitWithStarSystemsWithoutGalaxyIdGetResponse> orbitWithStarSystemsList = new LinkedList<>();
        orbitRepository.findOrbitsByGalaxyId(galaxyId).forEach(
                o -> orbitWithStarSystemsList.add(
                        mapper.map(orbitService.getOrbitWithSystemList(o.getOrbitId()),
                                OrbitWithStarSystemsWithoutGalaxyIdGetResponse.class)
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
    public GalaxyGetResponse createGalaxy(GalaxyCreateRequest galaxyCreateRequest) {
        galaxyValidatorService.validatePostRequest(galaxyCreateRequest);
        Galaxy galaxy = mapper.map(galaxyCreateRequest, Galaxy.class);
        Integer savedGalaxyId = galaxyRepository.save(galaxy).getGalaxyId();
        for (OrbitWithStarSystemsCreateRequest orbit : galaxyCreateRequest.getOrbitList()) {
            orbitService.createOrbit(savedGalaxyId, orbit);
        }
        return getGalaxyById(savedGalaxyId);
    }

    @CacheEvict(cacheNames = "galaxiesCache", key = "#galaxyId")
    public Galaxy updateGalaxy(Integer galaxyId, GalaxyDTO galaxy) {
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
