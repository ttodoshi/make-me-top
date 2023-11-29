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
    private Long groupId;
    private Long keeperId;
    private Long courseId;
    private List<ExplorerBaseInfoDto> explorers;
}
