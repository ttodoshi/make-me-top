package org.example.person.dto.courserequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseRegistrationRequestDto {
    private Long requestId;
    private Long courseId;
    private Long personId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    private CourseRegistrationRequestStatusDto status;
    private Long statusId;
}
