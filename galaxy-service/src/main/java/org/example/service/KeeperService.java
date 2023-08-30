package org.example.service;

import org.example.dto.course.CourseGetResponse;
import org.example.dto.keeper.KeeperWithSystemsDTO;

import java.util.List;

public interface KeeperService {
    List<KeeperWithSystemsDTO> getKeepersWithSystems(List<CourseGetResponse> courses);
}
