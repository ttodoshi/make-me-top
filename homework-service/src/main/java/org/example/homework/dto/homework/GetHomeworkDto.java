package org.example.homework.dto.homework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework.dto.group.GetExplorerGroupDto;
import org.example.homework.dto.homeworkrequest.GetHomeworkRequestWithPersonInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkDto {
    private Long homeworkId;
    private Long courseThemeId;
    private String title;
    private String content;
    private GetExplorerGroupDto group;
    private Long waitingRequestsCount;
    private List<GetHomeworkRequestWithPersonInfoDto> requests;
}
