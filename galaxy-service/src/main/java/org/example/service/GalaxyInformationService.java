package org.example.service;

import org.example.dto.galaxy.GetGalaxyInformationDto;
import org.example.model.Galaxy;

public interface GalaxyInformationService {
    GetGalaxyInformationDto getGalaxyInformation(Galaxy galaxy);
}
