package org.example.homework.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework.dto.explorer.ExplorerBaseInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetExplorerGroupDto {
    private Integer groupId;
    private Integer keeperId;
    private Integer courseId;
    private List<ExplorerBaseInfoDto> explorers;
}
