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
    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger(GalaxyService.class.getName());
    @Setter
    private String token;
    @Value("${course_app_url}")
    private String COURSE_APP_URL;

    public GetGalaxyRequest getGalaxyById(Integer id) {
        try {
            GetGalaxyRequest galaxy = mapper.map(galaxyRepository.getReferenceById(id), GetGalaxyRequest.class);
            galaxy.setOrbitsList(orbitRepository.findOrbitsByGalaxyId(id)
                    .stream()
                    .map(orbit -> mapper.map(orbit, GetOrbitWithStarSystemsWithoutGalaxyId.class))
                    .collect(Collectors.toList()));
            List<GetOrbitWithStarSystemsWithoutGalaxyId> orbitWithStarSystemsList = new LinkedList<>();
            for (GetOrbitWithStarSystemsWithoutGalaxyId orbitWithStarSystems : galaxy.getOrbitsList())
                orbitWithStarSystemsList.add(mapper.map(orbitService.getOrbitWithSystemList(orbitWithStarSystems.getOrbitId()), GetOrbitWithStarSystemsWithoutGalaxyId.class));
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
            for (CreateOrbitWithStarSystems orbit : createGalaxyRequest.getOrbitsList()) {
                Orbit savedOrbit = mapper.map(orbit, Orbit.class);
                savedOrbit.setGalaxyId(savedGalaxyId);
                Integer savedOrbitId = orbitRepository.save(savedOrbit).getOrbitId();
                if (orbit.getSystemsList() != null) {
                    for (CreateStarSystemWithoutOrbitId system : orbit.getSystemsList()) {
                        StarSystem savedSystem = mapper.map(system, StarSystem.class);
                        savedSystem.setOrbitId(savedOrbitId);
                        Integer savedSystemId = starSystemRepository.save(savedSystem).getSystemId();
                        createCourse(savedSystemId, system);
                    }
                }
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

    public Map<String, String> deleteGalaxy(Integer galaxyId) {
        try {
            galaxyRepository.deleteById(galaxyId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Галактика " + galaxyId + " была уничтожена квазаром");
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
