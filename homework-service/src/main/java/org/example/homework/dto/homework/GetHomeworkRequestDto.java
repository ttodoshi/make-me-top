package org.example.homework.dto.homework;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework.model.HomeworkFeedback;
import org.example.homework.model.HomeworkRequestStatusType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkRequestDto {
    private Integer requestId;
    private Integer homeworkId;
    private String content;
    private Integer explorerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    private Integer statusId;
    private HomeworkRequestStatusType status;
    private List<HomeworkFeedback> feedback;
}
