package org.example.repository.custom;

import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.galaxy.GalaxyInformationGetResponse;

import java.util.List;

public interface GalaxyRepository {
    List<GalaxyInformationGetResponse> getGalaxies();

    GalaxyDTO getGalaxyBySystemId(Integer systemId);
}
