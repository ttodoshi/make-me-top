package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.course.CourseDTO;
import org.example.dto.galaxy.CreateGalaxyRequest;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.galaxy.GetGalaxyRequest;
import org.example.dto.orbit.CreateOrbitWithStarSystems;
import org.example.dto.orbit.GetOrbitWithStarSystemsWithoutGalaxyId;
import org.example.dto.starsystem.CreateStarSystemWithoutOrbitId;
import org.example.exception.classes.galaxyEX.GalaxyAlreadyExistsException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.model.Galaxy;
import org.example.model.Orbit;
import org.example.model.StarSystem;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GalaxyService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final OrbitService orbitService;

    private final ModelMapper mapper;
    private final RestTemplate restTemplate;
    @Setter
    private String token;
    @Value("${course_app_url}")
    private String COURSE_APP_URL;

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
        Galaxy galaxy = mapper.map(createGalaxyRequest, Galaxy.class);
        Integer savedGalaxyId = galaxyRepository.save(galaxy).getGalaxyId();
        for (CreateOrbitWithStarSystems orbit : createGalaxyRequest.getOrbitsList()) {
            Orbit savedOrbit = mapper.map(orbit, Orbit.class);
            savedOrbit.setGalaxyId(savedGalaxyId);
            Integer savedOrbitId = orbitRepository.save(savedOrbit).getOrbitId();
            for (CreateStarSystemWithoutOrbitId system : orbit.getSystemsList()) {
                StarSystem savedSystem = mapper.map(system, StarSystem.class);
                savedSystem.setOrbitId(savedOrbitId);
                Integer savedSystemId = starSystemRepository.save(savedSystem).getSystemId();
                createCourse(savedSystemId, system);
            }
        }
        return getGalaxyById(savedGalaxyId);
    }

    private void createCourse(Integer courseId, CreateStarSystemWithoutOrbitId starSystem) {
        HttpEntity<CourseDTO> entity = new HttpEntity<>(new CourseDTO(courseId,
                starSystem.getSystemName(), new Date(), new Date(), starSystem.getDescription()),
                createHeaders());
        restTemplate.postForEntity(COURSE_APP_URL + "/course/", entity, CourseDTO.class);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
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
