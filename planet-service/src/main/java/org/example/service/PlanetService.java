package org.example.service;


import lombok.SneakyThrows;
import org.example.config.MapperConfig;
import org.example.exception.SystemNotFoundException;
import org.example.exception.connecntExceprion.ConnectException;
import org.example.exception.planetException.PlanetAlreadyExists;
import org.example.exception.planetException.PlanetNotFoundException;
import org.example.model.PlanetDAO;
import org.example.model.PlanetModel;
import org.example.repository.PlanetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@PropertySource(value={"classpath:config.properties"})
public class PlanetService {

    @Autowired
    PlanetRepository planetRepository;
    @Autowired
    MapperConfig mapperConfig;
    @Autowired
    JdbcTemplate jdbcTemplate;
    private StringBuilder QUERY_GALAXY;
    List<PlanetDAO> planetDAOList;
    PlanetDAO planetDAO;

    @Value("${app_galaxy_url}")
    private String APP_GALAXY_URL;
    @Value("${get_system_by_id}")
    private String GET_SYSTEM_BY_ID_URL;

    public List<PlanetModel> getListPlanetBySystemId(Integer systemId) {
        try {
            checkSystemExist(systemId);
            return planetRepository.getListPlanetBySystemId(systemId)
                    .stream().map(x -> mapperConfig.getMapper().map(x, PlanetModel.class)).collect(Collectors.toList());
        } catch (Exception e) {
            throw new SystemNotFoundException();
        }
    }


    public void addPlanet(List<PlanetModel> list, Integer galaxyId) {
        QUERY_GALAXY = new StringBuilder("INSERT INTO planet VALUES");
        planetDAOList = planetRepository.checkPlanetExists(galaxyId);
        for (PlanetModel model : list) {
            if (planetDAOList.stream().allMatch(x -> !Objects.equals(x.getPlanetName(), model.getPlanetName()))) {
                checkSystemExist(model.getSystemId());
                QUERY_GALAXY.append("(")
                        .append(model.getPlanetId())
                        .append(",'")
                        .append(model.getPlanetName())
                        .append("',")
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
            throw new PlanetAlreadyExists();
        }
    }

    public void deletePlanetById(Integer planetId) {
        try {
            planetRepository.deleteById(planetId);
        } catch (Exception e) {
            throw new PlanetNotFoundException();
        }
    }

    public void updateSystem(Integer planetId, Integer galaxyId, PlanetModel model) {
        try {
            planetDAO = planetRepository.getReferenceById(planetId);
            planetDAO.setPlanetId(model.getPlanetId());
            checkSystemExist(model.getSystemId());
            planetDAO.setSystemId(model.getSystemId());
            planetDAOList = planetRepository.checkPlanetExists(galaxyId);
        } catch (Exception e) {
            throw new PlanetNotFoundException();
        }
        if (planetDAOList.stream().allMatch(x -> !Objects.equals(x.getPlanetName(), model.getPlanetName()))) {
            planetDAO.setPlanetName(model.getPlanetName());
        } else {
            throw new PlanetAlreadyExists();
        }
        try {
            planetRepository.save(planetDAO);
        } catch (Exception e) {
            throw new ConnectException();
        }
    }


    @SneakyThrows
    private void checkSystemExist(Integer systemId) {

        var getSystemById = new Request.Builder()
                .get()
                .url(APP_GALAXY_URL + GET_SYSTEM_BY_ID_URL + systemId)
                .build();
        try (var response = new OkHttpClient().newCall(getSystemById).execute()) {
            if (response.code() != 200) {
                throw new SystemNotFoundException();
            }
        } catch (Exception e) {
            throw new ConnectException();
        }
    }
}
