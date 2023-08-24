package org.example.dto.homework;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.homework.HomeworkRequestStatusType;
import org.example.model.homework.HomeworkResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkRequest {
    private Integer requestId;
    private Integer homeworkId;
    private String content;
    private Integer keeperId;
    private Integer explorerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime requestDate;
    private Integer statusId;
    private HomeworkRequestStatusType status;
    private List<HomeworkResponse> responses;
}
