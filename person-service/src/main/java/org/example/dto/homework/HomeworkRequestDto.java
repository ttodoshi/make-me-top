package org.example.dto.homework;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HomeworkRequestDto {
    private Integer requestId;
    private Integer homeworkId;
    private String content;
    private Integer explorerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    private Integer statusId;
}
