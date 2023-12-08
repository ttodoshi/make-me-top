package org.example.homework.dto.homework;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework.dto.homeworkrequest.GetHomeworkRequestWithVersionsDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkWithRequestDto {
    private Long homeworkId;
    private String title;
    private String content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GetHomeworkRequestWithVersionsDto request;
}
