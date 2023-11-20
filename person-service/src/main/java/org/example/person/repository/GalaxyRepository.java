package org.example.person.repository;

import org.example.person.dto.galaxy.GalaxyDto;
import org.example.person.dto.galaxy.GetGalaxyInformationDto;

import java.util.List;

public interface GalaxyRepository {
    List<GetGalaxyInformationDto> findAll();

    GalaxyDto findGalaxyBySystemId(Integer systemId);
}
