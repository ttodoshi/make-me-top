package org.example.repository;

import org.example.dto.galaxy.GalaxyDto;
import org.example.dto.galaxy.GetGalaxyInformationDto;

import java.util.List;

public interface GalaxyRepository {
    List<GetGalaxyInformationDto> getGalaxies();

    GalaxyDto getGalaxyBySystemId(Integer systemId);
}
