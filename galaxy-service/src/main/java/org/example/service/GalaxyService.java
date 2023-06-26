package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.mapper.DependencyMapper;
import org.example.dto.galaxy.CreateGalaxyRequest;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.galaxy.GetGalaxyRequest;
import org.example.dto.orbit.OrbitWithStarSystemsAndDependencies;
import org.example.dto.orbit.OrbitWithStarSystemsWithoutGalaxyId;
import org.example.dto.starsystem.GetStarSystemWithDependencies;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.exception.classes.galaxyEX.GalaxyAlreadyExistsException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.model.Galaxy;
import org.example.model.Orbit;
import org.example.model.StarSystem;
import org.example.repository.DependencyRepository;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalaxyService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final OrbitService orbitService;

    private final ModelMapper mapper;

    private final Logger logger = Logger.getLogger(GalaxyService.class.getName());

    public GetGalaxyRequest getGalaxyById(Integer id) {
        try {
            GetGalaxyRequest galaxy = mapper.map(galaxyRepository.getReferenceById(id), GetGalaxyRequest.class);
            galaxy.setOrbitsList(orbitRepository.getOrbitsByGalaxyId(id)
                    .stream()
                    .map(orbit -> mapper.map(orbit, OrbitWithStarSystemsWithoutGalaxyId.class))
                    .collect(Collectors.toList()));
            List<OrbitWithStarSystemsWithoutGalaxyId> orbitWithStarSystemsList = new LinkedList<>();
            for (OrbitWithStarSystemsWithoutGalaxyId orbitWithStarSystems: galaxy.getOrbitsList())
                orbitWithStarSystemsList.add(mapper.map(orbitService.getOrbitWithSystemList(orbitWithStarSystems.getOrbitId()), OrbitWithStarSystemsWithoutGalaxyId.class));
            galaxy.setOrbitsList(orbitWithStarSystemsList);
            return galaxy;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }
    }

    @Transactional
    public GetGalaxyRequest createGalaxy(CreateGalaxyRequest createGalaxyRequest) {
        Galaxy galaxy = new Galaxy();
        galaxy.setGalaxyName(createGalaxyRequest.getGalaxyName());
        Integer savedGalaxyId = galaxyRepository.save(galaxy).getGalaxyId();
        if (createGalaxyRequest.getOrbitsList() != null) {
            for (OrbitWithStarSystemsAndDependencies orbit : createGalaxyRequest.getOrbitsList()) {
                orbit.setGalaxyId(savedGalaxyId);
                Integer savedOrbitId = orbitRepository.save(mapper.map(orbit, Orbit.class)).getOrbitId();
                if (orbit.getSystemsList() != null) {
                    for (StarSystemDTO system : orbit.getSystemsList()) {
                        system.setOrbitId(savedOrbitId);
                        starSystemRepository.save(mapper.map(system, StarSystem.class));
                    }
                }
            }
        }
        return getGalaxyById(savedGalaxyId);
    }

    public Galaxy updateGalaxy(Integer id, GalaxyDTO galaxy) {
        try {
            Galaxy updatedGalaxy = galaxyRepository.getReferenceById(id);
            updatedGalaxy.setGalaxyName(galaxy.getGalaxyName());
            return galaxyRepository.save(updatedGalaxy);
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            throw new GalaxyAlreadyExistsException();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }
    }

    public Map<String, String> deleteGalaxy(Integer id) {
        try {
            galaxyRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Галактика была уничтожена квазаром");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }
    }

    public List<Galaxy> getAllGalaxies() {
        return galaxyRepository.getAllGalaxy();
    }
}
