package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.exception.SystemNotFoundException;
import org.example.exception.connecntExceprion.ConnectException;
import org.example.exception.planetException.PlanetAlreadyExists;
import org.example.exception.planetException.PlanetNotFoundException;
import org.example.model.PlanetDAO;
import org.example.model.PlanetModel;
import org.example.repository.PlanetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PropertySource(value = {"classpath:config.properties"})
public class PlanetService {
    @Setter
    private String authHeader;

    private final PlanetRepository planetRepository;

    private final ModelMapper mapper;

    private final JdbcTemplate jdbcTemplate;

    private final Logger logger = Logger.getLogger(PlanetService.class.getName());

    @Value("${app_galaxy_url}")
    private String APP_GALAXY_URL;
    @Value("${get_system_by_id}")
    private String GET_SYSTEM_BY_ID_URL;

    public List<PlanetModel> getPlanetsListBySystemId(Integer systemId) {
        try {
            checkSystemExist(systemId);
            return planetRepository.getListPlanetBySystemId(systemId)
                    .stream()
                    .map(
                            x -> mapper
                                    .map(x, PlanetModel.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemNotFoundException();
        }
    }

    public void addPlanet(List<PlanetModel> list, Integer galaxyId) {
        StringBuilder QUERY_GALAXY = new StringBuilder("INSERT INTO planet VALUES");
        List<PlanetDAO> planetDAOList = planetRepository.checkPlanetExists(galaxyId);
        for (PlanetModel model : list) {
            if (planetDAOList.stream()
                    .allMatch(
                            x -> !Objects.equals(
                                    x.getPlanetName(), model.getPlanetName()))) {
                checkSystemExist(model.getSystemId());
                QUERY_GALAXY.append("(")
                        .append(model.getPlanetId())
                        .append(",'")
                        .append(model.getPlanetName())
                        .append("',")
                        .append(model.getPlanetNumber())
                        .append(",")
                        .append(model.getSystemId())
                        .append("),");
            } else {
                throw new PlanetAlreadyExists();
            }
        }

        QUERY_GALAXY.replace(QUERY_GALAXY.length() - 1, QUERY_GALAXY.length(), ";");
        try {
            jdbcTemplate.execute(QUERY_GALAXY.toString());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new PlanetAlreadyExists();
        }
    }

    public void deletePlanetById(Integer planetId) {
        try {
            planetRepository.deleteById(planetId);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new PlanetNotFoundException();
        }
    }

    public void updateSystem(Integer planetId, Integer galaxyId, PlanetModel model) {
        List<PlanetDAO> planetDAOList;
        PlanetDAO planetDAO;
        try {
            planetDAO = planetRepository.getReferenceById(planetId);
            planetDAO.setPlanetId(model.getPlanetId());
            checkSystemExist(model.getSystemId());
            planetDAO.setSystemId(model.getSystemId());
            planetDAOList = planetRepository.checkPlanetExists(galaxyId);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new PlanetNotFoundException();
        }
        try {
            if (planetDAOList.stream().allMatch(x -> !Objects.equals(x.getPlanetName(), model.getPlanetName()))) {
                planetDAO.setPlanetName(model.getPlanetName());
            } else {
                throw new PlanetAlreadyExists();
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        try {
            planetRepository.save(planetDAO);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new ConnectException();
        }
    }

    @SneakyThrows
    private void checkSystemExist(Integer systemId) {
        var getSystemById = new Request.Builder()
                .get()
                .header("Authorization", authHeader)
                .url(APP_GALAXY_URL + GET_SYSTEM_BY_ID_URL + systemId + "?withDependency=true")
                .build();
        try (var response = new OkHttpClient().newCall(getSystemById).execute()) {
            if (response.code() != HttpStatus.OK.value()) {
                throw new SystemNotFoundException();
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new ConnectException();
        }
    }
}
