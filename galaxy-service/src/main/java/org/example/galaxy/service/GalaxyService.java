package org.example.galaxy.service;

import org.example.galaxy.dto.galaxy.*;
import org.example.galaxy.dto.message.MessageDto;

import java.util.List;
import java.util.Map;

public interface GalaxyService {
    GetGalaxyDto findGalaxyById(Long galaxyId);

    GetGalaxyInformationDto findGalaxyByIdDetailed(Long galaxyId);

    List<GalaxyDto> findAllGalaxies();

    GalaxyDto findGalaxyBySystemId(Long systemId);

    Map<Long, GalaxyDto> findGalaxyBySystemIdIn(List<Long> systemIds);

    Long createGalaxy(CreateGalaxyDto createGalaxyRequest);

    GalaxyDto updateGalaxy(Long galaxyId, UpdateGalaxyDto galaxy);

    MessageDto deleteGalaxy(Long galaxyId);
}
