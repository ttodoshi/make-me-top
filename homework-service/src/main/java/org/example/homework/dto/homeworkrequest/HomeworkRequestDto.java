package org.example.homework.dto.homeworkrequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.example.homework.dto.homeworkmark.HomeworkMarkDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HomeworkRequestDto {
    private Long requestId;
    private Long homeworkId;
    private Long explorerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HomeworkRequestStatusDto status;
    private Long statusId;
    private List<HomeworkRequestVersionDto> homeworkRequestVersions;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HomeworkMarkDto mark;
}
