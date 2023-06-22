package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.dto.PlanetDTO;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.planetEX.PlanetAlreadyExistsException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.Planet;
import org.example.repository.PlanetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@PropertySource(value = {"classpath:config.properties"})
public class PlanetService {
    @Setter
    private String token;

    private final PlanetRepository planetRepository;

    private final ModelMapper mapper;

    private final Logger logger = Logger.getLogger(PlanetService.class.getName());

    @Value("${app_galaxy_url}")
    private String APP_GALAXY_URL;
    @Value("${get_system_by_id}")
    private String GET_SYSTEM_BY_ID_URL;

    public List<Planet> getPlanetsListBySystemId(Integer systemId) {
        if (checkSystemExists(systemId))
            return planetRepository.getListPlanetBySystemId(systemId);
        throw new SystemNotFoundException();
    }

    @Transactional
    public List<Planet> addPlanet(List<PlanetDTO> planets, Integer galaxyId) {
        List<Planet> savedPlanets = new LinkedList<>();
        List<Planet> planetsByGalaxyId;
        try {
            planetsByGalaxyId = planetRepository.getAllPlanetsByGalaxyId(galaxyId);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }
        for (PlanetDTO planet : planets) {
            if (planetsByGalaxyId.stream().noneMatch(
                    x -> Objects.equals(x.getPlanetName(), planet.getPlanetName()))) {
                if (checkSystemExists(planet.getSystemId()))
                    savedPlanets.add(planetRepository.save(mapper.map(planet, Planet.class)));
                else
                    throw new SystemNotFoundException();
            } else
                throw new PlanetAlreadyExistsException();
        }
        return savedPlanets;
    }

    public Map<String, String> deletePlanetById(Integer planetId) {
        try {
            planetRepository.deleteById(planetId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Планета успешно удалена");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new PlanetNotFoundException();
        }
    }

    public Planet updatePlanet(Integer planetId,
                               Integer galaxyId,
                               PlanetDTO planet) {
        if (!checkSystemExists(planet.getSystemId()))
            throw new SystemNotFoundException();
        Optional<Planet> updatedPlanetOptional = planetRepository.findById(planetId);
        Planet updatedPlanet;
        if (updatedPlanetOptional.isEmpty())
            throw new PlanetNotFoundException();
        else
            updatedPlanet = updatedPlanetOptional.get();
        try {
            planetRepository.getAllPlanetsByGalaxyId(galaxyId);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }
        updatedPlanet.setPlanetName(planet.getPlanetName());
        updatedPlanet.setSystemId(planet.getSystemId());
        updatedPlanet.setPlanetNumber(planet.getPlanetNumber());
        return planetRepository.save(updatedPlanet);
    }

    @SneakyThrows
    private boolean checkSystemExists(Integer systemId) {
        var getSystemById = new Request.Builder()
                .get()
                .header("Authorization", token)
                .url(APP_GALAXY_URL + GET_SYSTEM_BY_ID_URL + systemId + "?withDependency=true")
                .build();
        try (var response = new OkHttpClient().newCall(getSystemById).execute()) {
            if (response.code() != HttpStatus.OK.value())
                return false;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new ConnectException();
        }
        return true;
    }
}
