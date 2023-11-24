package org.example.homework.dto.homework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework.dto.group.GetExplorerGroupDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkDto {
    private Integer homeworkId;
    private Integer courseThemeId;
    private String content;
    private GetExplorerGroupDto group;
    private Long waitingRequestsCount;
    private List<GetHomeworkRequestWithPersonInfoDto> requests;
}
