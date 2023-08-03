package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.GalaxyCreateRequest;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.galaxy.GalaxyGetResponse;
import org.example.dto.orbit.OrbitWithStarSystemsCreateRequest;
import org.example.dto.orbit.OrbitWithStarSystemsWithoutGalaxyIdGetResponse;
import org.example.model.Galaxy;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.validator.GalaxyValidator;
import org.modelmapper.ModelMapper;
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

    private final GalaxyValidator galaxyValidator;
    private final OrbitService orbitService;

    private final ModelMapper mapper;

    public List<Galaxy> getAllGalaxies() {
        return galaxyRepository.findAll();
    }

    @Transactional
    public GalaxyGetResponse getGalaxyById(Integer galaxyId) {
        galaxyValidator.validateGetByIdRequest(galaxyId);
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

    @Transactional
    public GalaxyGetResponse createGalaxy(GalaxyCreateRequest galaxyCreateRequest) {
        galaxyValidator.validatePostRequest(galaxyCreateRequest);
        Galaxy galaxy = mapper.map(galaxyCreateRequest, Galaxy.class);
        Integer savedGalaxyId = galaxyRepository.save(galaxy).getGalaxyId();
        for (OrbitWithStarSystemsCreateRequest orbit : galaxyCreateRequest.getOrbitList()) {
            orbitService.createOrbit(savedGalaxyId, orbit);
        }
        return getGalaxyById(savedGalaxyId);
    }

    public Galaxy updateGalaxy(Integer galaxyId, GalaxyDTO galaxy) {
        galaxyValidator.validatePutRequest(galaxyId, galaxy);
        Galaxy updatedGalaxy = galaxyRepository.getReferenceById(galaxyId);
        updatedGalaxy.setGalaxyName(galaxy.getGalaxyName());
        return galaxyRepository.save(updatedGalaxy);
    }

    public Map<String, String> deleteGalaxy(Integer galaxyId) {
        galaxyValidator.validateDeleteRequest(galaxyId);
        galaxyRepository.deleteById(galaxyId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Галактика " + galaxyId + " была уничтожена квазаром");
        return response;
    }
}
