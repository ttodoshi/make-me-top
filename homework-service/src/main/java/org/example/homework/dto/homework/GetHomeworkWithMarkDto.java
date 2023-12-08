package org.example.homework.dto.homework;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.homework.dto.homeworkmark.HomeworkMarkDto;
import org.example.homework.dto.homeworkrequest.HomeworkRequestStatusDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetHomeworkWithMarkDto extends HomeworkDto {
    private HomeworkRequestStatusDto status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HomeworkMarkDto mark;

    public GetHomeworkWithMarkDto(HomeworkDto homework, HomeworkRequestStatusDto status, HomeworkMarkDto mark) {
        super(homework.getHomeworkId(), homework.getCourseThemeId(), homework.getTitle(), homework.getContent(), homework.getGroupId());
        this.status = status;
        this.mark = mark;
    }
}
