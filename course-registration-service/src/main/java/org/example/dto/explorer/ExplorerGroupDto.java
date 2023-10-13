package org.example.dto.explorer;

import lombok.Data;

import java.util.List;

@Data
public class ExplorerGroupDto {
    private Integer groupId;
    private Integer courseId;
    private Integer keeperId;
    private List<ExplorerDto> explorers;
}
