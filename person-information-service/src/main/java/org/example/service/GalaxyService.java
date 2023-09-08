package org.example.service;

import org.example.dto.galaxy.GalaxyDto;
import org.example.dto.galaxy.GetGalaxyInformationDto;

import java.util.List;

public interface GalaxyService {
    List<GetGalaxyInformationDto> getGalaxies();

    GalaxyDto getGalaxyBySystemId(Integer systemId);
}
