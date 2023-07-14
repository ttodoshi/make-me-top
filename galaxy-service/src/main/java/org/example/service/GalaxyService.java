package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.galaxy.CreateGalaxyRequest;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.galaxy.GetGalaxyRequest;
import org.example.dto.orbit.CreateOrbitWithStarSystems;
import org.example.dto.orbit.GetOrbitWithStarSystemsWithoutGalaxyId;
import org.example.exception.classes.galaxyEX.GalaxyAlreadyExistsException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.model.Galaxy;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GalaxyService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;

    private final OrbitService orbitService;

    private final ModelMapper mapper;

    @Setter
    private String token;

    public List<Galaxy> getAllGalaxies() {
        return galaxyRepository.findAll();
    }

    public GetGalaxyRequest getGalaxyById(Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException();
        GetGalaxyRequest galaxy = mapper.map(galaxyRepository.getReferenceById(galaxyId), GetGalaxyRequest.class);
        List<GetOrbitWithStarSystemsWithoutGalaxyId> orbitWithStarSystemsList = new LinkedList<>();
        orbitRepository.findOrbitsByGalaxyId(galaxyId).forEach(
                o -> orbitWithStarSystemsList.add(
                        mapper.map(
                                orbitService.getOrbitWithSystemList(o.getOrbitId()),
                                GetOrbitWithStarSystemsWithoutGalaxyId.class)
                )
        );
        galaxy.setOrbitsList(orbitWithStarSystemsList);
        return galaxy;
    }

    @Transactional
    public GetGalaxyRequest createGalaxy(CreateGalaxyRequest createGalaxyRequest) {
        if (galaxyRepository.findAll().stream()
                .anyMatch(g -> g.getGalaxyName().equals(createGalaxyRequest.getGalaxyName()))) {
            throw new GalaxyAlreadyExistsException();
        }
        Galaxy galaxy = mapper.map(createGalaxyRequest, Galaxy.class);
        Integer savedGalaxyId = galaxyRepository.save(galaxy).getGalaxyId();
        orbitService.setToken(token);
        for (CreateOrbitWithStarSystems orbit : createGalaxyRequest.getOrbitsList()) {
            orbitService.createOrbit(savedGalaxyId, orbit);
        }
        return getGalaxyById(savedGalaxyId);
    }

    public Galaxy updateGalaxy(Integer galaxyId, GalaxyDTO galaxy) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException();
        if (galaxyRepository.findAll().stream()
                .anyMatch(g -> g.getGalaxyName().equals(galaxy.getGalaxyName()))) {
            throw new GalaxyAlreadyExistsException();
        }
        Galaxy updatedGalaxy = galaxyRepository.getReferenceById(galaxyId);
        updatedGalaxy.setGalaxyName(galaxy.getGalaxyName());
        return galaxyRepository.save(updatedGalaxy);
    }

    public Map<String, String> deleteGalaxy(Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException();
        galaxyRepository.deleteById(galaxyId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Галактика " + galaxyId + " была уничтожена квазаром");
        return response;
    }
}
