package org.example.homework.dto.homework;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework.model.HomeworkMark;
import org.example.homework.model.HomeworkRequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkRequestWithVersionsDto {
    private Long requestId;
    private Long homeworkId;
    private Long explorerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    private HomeworkRequestStatus status;
    private List<GetHomeworkRequestVersionDto> homeworkRequestVersions;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HomeworkMark mark;
}
