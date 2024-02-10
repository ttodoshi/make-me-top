package org.example.galaxy.service;

import org.example.galaxy.dto.dependency.CreateDependencyDto;
import org.example.galaxy.dto.dependency.DependencyDto;
import org.example.galaxy.dto.message.MessageDto;

import java.util.List;

public interface SystemDependencyService {
    List<Long> addDependency(List<CreateDependencyDto> systemDependencies);

    MessageDto deleteDependency(DependencyDto dependency);
}
