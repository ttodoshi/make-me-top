package org.example.dto.courserequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseRegistrationRequestDto {
    private Integer requestId;
    private Integer courseId;
    private Integer personId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    private CourseRegistrationRequestStatusDto status;
    private Integer statusId;
}
