package org.example.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerGroupDto {
    private Integer groupId;
    private Integer courseId;
    private Integer keeperId;
    private List<ExplorerDto> explorers;
}
