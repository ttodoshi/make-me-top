package org.example.progress.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.progress.dto.explorer.ExplorerBasicInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentKeeperGroupDto {
    private Long groupId;
    private Long courseId;
    private Long keeperId;
    private String courseTitle;
    private List<ExplorerBasicInfoDto> explorers;
}
