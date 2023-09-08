package org.example.service;

import org.example.dto.starsystem.StarSystemDto;

import java.util.List;

public interface StarSystemService {
    List<StarSystemDto> getSystemsByGalaxyId(Integer galaxyId);
}
