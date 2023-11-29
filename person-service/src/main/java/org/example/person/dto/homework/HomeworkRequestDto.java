package org.example.person.dto.homework;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeworkRequestDto {
    private Long requestId;
    private Long homeworkId;
    private Long explorerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    private HomeworkRequestStatusDto status;
    private Long statusId;
    private List<HomeworkRequestVersionDto> homeworkRequestVersions;
}
